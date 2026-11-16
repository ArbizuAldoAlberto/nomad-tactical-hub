package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.BatteryManager
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import kotlin.math.sqrt

class HardwareSensorBridge(private val context: Context?) : SensorEventListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val sensorManager: SensorManager? =
        context?.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val locationManager: LocationManager? =
        context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private val cameraManager: CameraManager? =
        context?.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var cprMetronomeJob: Job? = null

    // --- 360° Reactive Hardware Telemetry Flows ---
    // Centered on San Carlos de Bolívar, Bs. As., Argentina (-36.2333, -61.1167) until satellite lock
    val latitude = MutableStateFlow(-36.2333)
    val longitude = MutableStateFlow(-61.1167)
    val altitudeMeters = MutableStateFlow(94.0)
    val gpsAccuracyMeters = MutableStateFlow(15.0f)
    val hasRealGpsLock = MutableStateFlow(false)

    // Magnetometer + Accelerometer True Compass & EMF Anomaly Detector
    val compassHeadingDeg = MutableStateFlow(0f)
    val magneticFieldMicroTesla = MutableStateFlow(48.5f)
    val emfAnomalyAlert = MutableStateFlow(false)

    // Barometer Weather Station
    val pressureHpa = MutableStateFlow(1013.25f)
    val pressure3hAgoHpa = MutableStateFlow(1013.25f)
    val barometerStormWarning = MutableStateFlow(false)

    // Seismograph & Dead-Man's Switch (Hombre Caído)
    val linearAccelerationG = MutableStateFlow(0.0f)
    val seismicAlertActive = MutableStateFlow(false)
    val deadManSwitchEnabled = MutableStateFlow(false)
    val deadManCountdownSec = MutableStateFlow(0)
    val deadManSosTriggered = MutableStateFlow(false)

    // Ambient Light & Battery
    val ambientLux = MutableStateFlow(120.0f)
    val batteryPercent = MutableStateFlow(85)

    // Hands-Free CPR Metronome (110 BPM) & Voice Triage
    val isCprMetronomeRunning = MutableStateFlow(false)
    val cprBeatCount = MutableStateFlow(0)

    private val gravityValues = FloatArray(3)
    private val geomagneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasMagnetic = false
    private var lastMovementTimestamp = System.currentTimeMillis()

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitude.value = location.latitude
            longitude.value = location.longitude
            if (location.hasAltitude()) altitudeMeters.value = location.altitude
            if (location.hasAccuracy()) gpsAccuracyMeters.value = location.accuracy
            hasRealGpsLock.value = true
        }
    }

    init {
        initTextToSpeech()
        refreshBatteryLevel()
    }

    private fun initTextToSpeech() {
        val ctx = context ?: return
        try {
            tts = TextToSpeech(ctx) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale("es", "AR")
                    ttsReady = true
                }
            }
        } catch (_: Exception) {
            ttsReady = false
        }
    }

    @SuppressLint("MissingPermission")
    fun startSensors() {
        refreshBatteryLevel()
        val sm = sensorManager ?: return
        try {
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            sm.getDefaultSensor(Sensor.TYPE_PRESSURE)?.let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            sm.getDefaultSensor(Sensor.TYPE_LIGHT)?.let {
                sm.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        } catch (_: Exception) {
        }

        // Request Raw GNSS (GPS_PROVIDER + NETWORK_PROVIDER) without requiring Google Play Services
        val lm = locationManager ?: return
        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000L, 2f, locationListener)
            }
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5f, locationListener)
            }
            val lastGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastGps != null) {
                latitude.value = lastGps.latitude
                longitude.value = lastGps.longitude
                hasRealGpsLock.value = true
            }
        } catch (_: SecurityException) {
            // Permission not granted yet; stays gently on Bolívar default coordinates
        } catch (_: Exception) {
        }
    }

    fun stopSensors() {
        try {
            sensorManager?.unregisterListener(this)
            locationManager?.removeUpdates(locationListener)
        } catch (_: Exception) {
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val ev = event ?: return
        when (ev.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val alpha = 0.85f
                gravityValues[0] = alpha * gravityValues[0] + (1 - alpha) * ev.values[0]
                gravityValues[1] = alpha * gravityValues[1] + (1 - alpha) * ev.values[1]
                gravityValues[2] = alpha * gravityValues[2] + (1 - alpha) * ev.values[2]
                hasGravity = true

                val lx = ev.values[0] - gravityValues[0]
                val ly = ev.values[1] - gravityValues[1]
                val lz = ev.values[2] - gravityValues[2]
                val linG = sqrt(lx * lx + ly * ly + lz * lz) / SensorManager.GRAVITY_EARTH
                linearAccelerationG.value = linG

                if (linG > 0.18f) {
                    lastMovementTimestamp = System.currentTimeMillis()
                    if (deadManCountdownSec.value > 0) deadManCountdownSec.value = 0
                }
                if (linG > 0.45f) {
                    seismicAlertActive.value = true
                }
                updateCompassHeading()
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                val filter = 0.20f
                geomagneticValues[0] = geomagneticValues[0] + filter * (ev.values[0] - geomagneticValues[0])
                geomagneticValues[1] = geomagneticValues[1] + filter * (ev.values[1] - geomagneticValues[1])
                geomagneticValues[2] = geomagneticValues[2] + filter * (ev.values[2] - geomagneticValues[2])
                hasMagnetic = true

                val magStrength = sqrt(
                    ev.values[0] * ev.values[0] +
                        ev.values[1] * ev.values[1] +
                        ev.values[2] * ev.values[2]
                )
                magneticFieldMicroTesla.value = magStrength
                emfAnomalyAlert.value = magStrength > 85.0f
                updateCompassHeading()
            }

            Sensor.TYPE_PRESSURE -> {
                val p = ev.values[0]
                pressureHpa.value = p
                barometerStormWarning.value = (p - pressure3hAgoHpa.value) <= -4.0f
            }

            Sensor.TYPE_LIGHT -> {
                ambientLux.value = ev.values[0]
            }
        }
    }

    private fun updateCompassHeading() {
        if (!hasGravity || !hasMagnetic) return
        val r = FloatArray(9)
        val i = FloatArray(9)
        if (SensorManager.getRotationMatrix(r, i, gravityValues, geomagneticValues)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(r, orientation)
            val azimuthRad = orientation[0]
            val deg = ((Math.toDegrees(azimuthRad.toDouble()) + 360.0) % 360.0).toFloat()
            compassHeadingDeg.value = deg
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Controls physical LED Flashlight (Torch Mode) for optical Morse transmission.
     */
    fun setTorchLight(enabled: Boolean, emconSilent: Boolean = false) {
        if (emconSilent && enabled) return
        val cm = cameraManager ?: return
        try {
            val cameraId = cm.cameraIdList.firstOrNull() ?: return
            cm.setTorchMode(cameraId, enabled)
        } catch (_: Exception) {
        }
    }

    /**
     * Hands-Free Offline Voice Guidance for Rescuers (Stop the Bleed, CPR, Burn Triage).
     */
    fun speakHandsFree(text: String, emconSilent: Boolean = false) {
        if (emconSilent || !ttsReady) return
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NOMAD_VOICE_GUIDE")
        } catch (_: Exception) {
        }
    }

    /**
     * Starts/Stops the 110 BPM Acoustic CPR Metronome (AHA/ERC Standard: 100-120 compressions/min).
     */
    fun toggleCprMetronome(enable: Boolean, emconSilent: Boolean = false) {
        cprMetronomeJob?.cancel()
        if (!enable || emconSilent) {
            isCprMetronomeRunning.value = false
            cprBeatCount.value = 0
            return
        }
        isCprMetronomeRunning.value = true
        speakHandsFree("Iniciando metrónomo R C P a 110 compresiones por minuto. Comprima fuerte y rápido en el centro del pecho.", emconSilent)
        cprMetronomeJob = scope.launch {
            val intervalMs = 545L // 60,000 ms / 110 BPM = 545.45 ms per compression
            var toneGen: ToneGenerator? = null
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 90)
            } catch (_: Exception) {
            }
            var count = 0
            while (isActive && isCprMetronomeRunning.value) {
                count = (count % 30) + 1
                cprBeatCount.value = count
                try {
                    val tone = if (count == 1 || count == 30) ToneGenerator.TONE_PROP_BEEP2 else ToneGenerator.TONE_PROP_BEEP
                    toneGen?.startTone(tone, 60)
                } catch (_: Exception) {
                }
                delay(intervalMs)
            }
            try {
                toneGen?.release()
            } catch (_: Exception) {
            }
        }
    }

    fun refreshBatteryLevel(): Int {
        val ctx = context ?: return batteryPercent.value
        return try {
            val intent = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level >= 0 && scale > 0) {
                val pct = ((level * 100f) / scale).toInt().coerceIn(1, 100)
                batteryPercent.value = pct
                pct
            } else {
                batteryPercent.value
            }
        } catch (_: Exception) {
            batteryPercent.value
        }
    }

    fun shutdown() {
        stopSensors()
        cprMetronomeJob?.cancel()
        setTorchLight(false)
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
        scope.cancel()
    }
}
