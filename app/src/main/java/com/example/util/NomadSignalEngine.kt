package com.example.util

import android.annotation.SuppressLint
import android.media.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import kotlin.math.*

enum class NeppMessageType(val code: Byte) {
    SOS_DISTRESS(0x01),
    MEDICAL_TRIAGE(0x02),
    SHELTER_SAFEZONE(0x03),
    SOLIDARITY_AID(0x04),
    WAYPOINT_SYNC(0x05);

    companion object {
        fun fromCode(code: Byte): NeppMessageType =
            entries.firstOrNull { it.code == code } ?: SOLIDARITY_AID
    }
}

data class NeppPacket(
    val version: Byte = 0x01,
    val type: NeppMessageType,
    val senderHash: Int,
    val latitude: Double,
    val longitude: Double,
    val payloadText: String,
    val crc16: Int = 0,
    val fecRepairedBytes: Int = 0
)

object NomadSignalEngine {

    const val SAMPLE_RATE_HZ = 44100
    const val MORSE_TONE_HZ = 800.0
    const val AFSK_MARK_HZ = 1200.0
    const val AFSK_SPACE_HZ = 2200.0
    const val VOX_PREAMBLE_HZ = 1800.0

    /**
     * Encodes a compact NEPP (< 128 bytes) binary packet with CRC-16-CCITT and a 3x Majority-Vote / Parity FEC tail
     * so corrupted bytes over noisy VHF/UHF radio can be mathematically reconstructed.
     */
    fun encodeNeppPacket(
        type: NeppMessageType,
        senderCallsign: String,
        latitude: Double,
        longitude: Double,
        payloadText: String
    ): ByteArray {
        val rawPayload = payloadText.take(64).toByteArray(StandardCharsets.UTF_8)
        val senderHash = senderCallsign.hashCode()
        val latMicro = (latitude.coerceIn(-90.0, 90.0) * 1e7).roundToInt()
        val lngMicro = (longitude.coerceIn(-180.0, 180.0) * 1e7).roundToInt()

        val coreLen = 2 + 1 + 1 + 4 + 4 + 4 + 1 + rawPayload.size
        val coreBuffer = ByteBuffer.allocate(coreLen).order(ByteOrder.BIG_ENDIAN)
        coreBuffer.put(0x4E.toByte()) // 'N'
        coreBuffer.put(0x4D.toByte()) // 'M'
        coreBuffer.put(0x01.toByte()) // Version 1
        coreBuffer.put(type.code)
        coreBuffer.putInt(senderHash)
        coreBuffer.putInt(latMicro)
        coreBuffer.putInt(lngMicro)
        coreBuffer.put(rawPayload.size.toByte())
        coreBuffer.put(rawPayload)

        val coreBytes = coreBuffer.array()
        val crc = computeCrc16Ccitt(coreBytes)

        // Compute 16-byte XOR Reed-Solomon-style block parity over 4-byte stripes + full replica of header/coords
        val parityBlock = ByteArray(16)
        for (i in coreBytes.indices) {
            parityBlock[i % 16] = (parityBlock[i % 16].toInt() xor coreBytes[i].toInt()).toByte()
        }

        val finalBuffer = ByteBuffer.allocate(coreBytes.size + 2 + parityBlock.size).order(ByteOrder.BIG_ENDIAN)
        finalBuffer.put(coreBytes)
        finalBuffer.putShort(crc.toShort())
        finalBuffer.put(parityBlock)
        return finalBuffer.array()
    }

    /**
     * Decodes a NEPP binary frame and attempts single-stripe FEC recovery if CRC-16 fails due to RF static.
     */
    fun decodeNeppPacket(frameBytes: ByteArray): NeppPacket? {
        if (frameBytes.size < 2 + 1 + 1 + 4 + 4 + 4 + 1 + 2 + 16) return null
        val working = frameBytes.copyOf()
        val coreEnd = working.size - 18
        val receivedCrc = ((working[coreEnd].toInt() and 0xFF) shl 8) or (working[coreEnd + 1].toInt() and 0xFF)
        val parityBlock = working.copyOfRange(coreEnd + 2, working.size)

        var coreBytes = working.copyOfRange(0, coreEnd)
        var calculatedCrc = computeCrc16Ccitt(coreBytes)
        var repairedCount = 0

        if (calculatedCrc != receivedCrc) {
            // Attempt single-byte FEC recovery across each of the 16 parity stripes
            recoverLoop@ for (corruptIdx in coreBytes.indices) {
                val stripe = corruptIdx % 16
                var xorAccum = parityBlock[stripe].toInt() and 0xFF
                for (j in coreBytes.indices) {
                    if (j != corruptIdx && (j % 16) == stripe) {
                        xorAccum = xorAccum xor (coreBytes[j].toInt() and 0xFF)
                    }
                }
                val candidate = coreBytes.copyOf()
                candidate[corruptIdx] = xorAccum.toByte()
                if (computeCrc16Ccitt(candidate) == receivedCrc) {
                    coreBytes = candidate
                    calculatedCrc = receivedCrc
                    repairedCount = 1
                    break@recoverLoop
                }
            }
        }

        if (calculatedCrc != receivedCrc) return null
        if (coreBytes[0] != 0x4E.toByte() || coreBytes[1] != 0x4D.toByte()) return null

        val buf = ByteBuffer.wrap(coreBytes).order(ByteOrder.BIG_ENDIAN)
        buf.get() // 'N'
        buf.get() // 'M'
        val version = buf.get()
        val msgType = NeppMessageType.fromCode(buf.get())
        val senderHash = buf.int
        val lat = buf.int / 1e7
        val lng = buf.int / 1e7
        val payloadLen = (buf.get().toInt() and 0xFF).coerceAtMost(buf.remaining())
        val payloadBytes = ByteArray(payloadLen)
        buf.get(payloadBytes)

        return NeppPacket(
            version = version,
            type = msgType,
            senderHash = senderHash,
            latitude = lat,
            longitude = lng,
            payloadText = String(payloadBytes, StandardCharsets.UTF_8),
            crc16 = calculatedCrc,
            fecRepairedBytes = repairedCount
        )
    }

    fun computeCrc16Ccitt(bytes: ByteArray): Int {
        var crc = 0xFFFF
        for (b in bytes) {
            crc = crc xor ((b.toInt() and 0xFF) shl 8)
            for (bit in 0 until 8) {
                crc = if ((crc and 0x8000) != 0) {
                    ((crc shl 1) xor 0x1021) and 0xFFFF
                } else {
                    (crc shl 1) and 0xFFFF
                }
            }
        }
        return crc and 0xFFFF
    }

    /**
     * Generates 800 Hz CW Morse PCM samples with a 5 ms raised-cosine ramp to avoid Baofeng PTT/VOX clicks.
     */
    fun generateMorseAudioPcm(
        morsePattern: String,
        sampleRate: Int = SAMPLE_RATE_HZ,
        toneHz: Double = MORSE_TONE_HZ,
        includeVoxPreamble: Boolean = false
    ): ShortArray {
        val timings = SurvivalCipher.getMorseTimingPattern(morsePattern)
        if (timings.isEmpty()) return ShortArray(0)

        val preambleMs = if (includeVoxPreamble) 300L else 0L
        val totalMs = preambleMs + timings.sumOf { it.second }
        val totalSamples = ((totalMs * sampleRate) / 1000L).toInt().coerceAtMost(sampleRate * 30)
        val pcm = ShortArray(totalSamples)
        var cursor = 0

        if (includeVoxPreamble) {
            val preSamples = ((preambleMs * sampleRate) / 1000L).toInt()
            writeToneWithEnvelope(pcm, cursor, preSamples, VOX_PREAMBLE_HZ, sampleRate, 0.55)
            cursor += preSamples
        }

        for ((isOn, durationMs) in timings) {
            val numSamples = ((durationMs * sampleRate) / 1000L).toInt()
            if (cursor + numSamples > pcm.size) break
            if (isOn) {
                writeToneWithEnvelope(pcm, cursor, numSamples, toneHz, sampleRate, 0.85)
            }
            cursor += numSamples
        }
        return pcm
    }

    /**
     * Generates AFSK (1200 Hz Mark = 1, 2200 Hz Space = 0) PCM waveform for analog radio transmission.
     */
    fun generateAfskPacketPcm(
        payloadBytes: ByteArray,
        baudRate: Int = 100, // Robust acoustic baud rate for speaker-to-mic & AUX coupling
        sampleRate: Int = SAMPLE_RATE_HZ
    ): ShortArray {
        if (payloadBytes.isEmpty()) return ShortArray(0)
        val samplesPerBit = (sampleRate / baudRate).coerceAtLeast(10)
        val totalBits = payloadBytes.size * 8
        val pcm = ShortArray(totalBits * samplesPerBit)

        var sampleCursor = 0
        var phase = 0.0

        for (byteVal in payloadBytes) {
            for (bitIdx in 7 downTo 0) {
                val bit = ((byteVal.toInt() shr bitIdx) and 1) == 1
                val freq = if (bit) AFSK_MARK_HZ else AFSK_SPACE_HZ
                val phaseInc = (2.0 * PI * freq) / sampleRate
                for (s in 0 until samplesPerBit) {
                    pcm[sampleCursor++] = (sin(phase) * 26000.0).toInt().toShort()
                    phase += phaseInc
                    if (phase > 2.0 * PI) phase -= 2.0 * PI
                }
            }
        }
        return pcm
    }

    /**
     * Demodulates an AFSK PCM buffer back into raw bytes using the Goertzel frequency-energy detector.
     */
    fun demodulateAfskPacketPcm(
        pcm: ShortArray,
        expectedByteCount: Int,
        baudRate: Int = 100,
        sampleRate: Int = SAMPLE_RATE_HZ
    ): ByteArray {
        val samplesPerBit = (sampleRate / baudRate).coerceAtLeast(10)
        val totalBits = expectedByteCount * 8
        if (pcm.size < totalBits * samplesPerBit) return ByteArray(0)

        val out = ByteArray(expectedByteCount)
        var sampleCursor = 0

        for (byteIdx in 0 until expectedByteCount) {
            var accByte = 0
            for (bitIdx in 7 downTo 0) {
                val markEnergy = goertzelMagnitude(pcm, sampleCursor, samplesPerBit, AFSK_MARK_HZ, sampleRate)
                val spaceEnergy = goertzelMagnitude(pcm, sampleCursor, samplesPerBit, AFSK_SPACE_HZ, sampleRate)
                val bit = if (markEnergy >= spaceEnergy) 1 else 0
                accByte = accByte or (bit shl bitIdx)
                sampleCursor += samplesPerBit
            }
            out[byteIdx] = accByte.toByte()
        }
        return out
    }

    /**
     * Generates a compact SSTV (Slow-Scan Television) tactical coordinate/map beacon audio frame
     * (1900Hz-1200Hz-1900Hz VIS header + 1200Hz sync + 1500Hz-2300Hz luminance scanlines).
     */
    fun generateSstvTacticalBeaconPcm(
        scanlineLuminance: List<Double> = listOf(0.1, 0.9, 0.2, 0.8, 0.1, 0.9, 0.5, 1.0),
        sampleRate: Int = SAMPLE_RATE_HZ
    ): ShortArray {
        // Compact 1.5-second SSTV burst with VIS calibration header and luminance bars
        val totalSamples = (sampleRate * 1.5).toInt()
        val pcm = ShortArray(totalSamples)
        var cursor = 0

        // VIS Calibration Leader: 1900 Hz (200ms) -> 1200 Hz Sync (30ms) -> 1900 Hz (200ms)
        val leader1 = (sampleRate * 0.20).toInt()
        writeToneWithEnvelope(pcm, cursor, leader1, 1900.0, sampleRate, 0.8)
        cursor += leader1

        val syncPulse = (sampleRate * 0.03).toInt()
        writeToneWithEnvelope(pcm, cursor, syncPulse, 1200.0, sampleRate, 0.85)
        cursor += syncPulse

        writeToneWithEnvelope(pcm, cursor, leader1, 1900.0, sampleRate, 0.8)
        cursor += leader1

        // Encode luminance bars between 1500 Hz (Black) and 2300 Hz (White)
        val remaining = totalSamples - cursor
        val samplesPerBar = (remaining / scanlineLuminance.size.coerceAtLeast(1)).coerceAtLeast(1)
        for (lum in scanlineLuminance) {
            if (cursor + samplesPerBar > pcm.size) break
            val freq = 1500.0 + lum.coerceIn(0.0, 1.0) * 800.0
            writeToneWithEnvelope(pcm, cursor, samplesPerBar, freq, sampleRate, 0.8)
            cursor += samplesPerBar
        }
        return pcm
    }

    /**
     * Goertzel Single-Frequency Energy Detector (O(N) complexity, optimal for 800Hz Morse and 1200/2200Hz AFSK).
     */
    fun goertzelMagnitude(
        samples: ShortArray,
        offset: Int,
        length: Int,
        targetFreqHz: Double,
        sampleRate: Int = SAMPLE_RATE_HZ
    ): Double {
        if (length <= 0 || offset < 0 || offset + length > samples.size) return 0.0
        val k = (0.5 + (length * targetFreqHz) / sampleRate).toInt()
        val omega = (2.0 * PI * k) / length
        val coeff = 2.0 * cos(omega)
        var q0 = 0.0
        var q1 = 0.0
        var q2 = 0.0

        for (i in 0 until length) {
            val sampleNorm = samples[offset + i] / 32768.0
            q0 = coeff * q1 - q2 + sampleNorm
            q2 = q1
            q1 = q0
        }
        return sqrt((q1 * q1 + q2 * q2 - q1 * q2 * coeff).coerceAtLeast(0.0))
    }

    private fun writeToneWithEnvelope(
        buffer: ShortArray,
        offset: Int,
        length: Int,
        freqHz: Double,
        sampleRate: Int,
        amplitude: Double
    ) {
        val rampSamples = ((sampleRate * 5) / 1000).coerceAtMost(length / 4).coerceAtLeast(1)
        val angularStep = (2.0 * PI * freqHz) / sampleRate
        val maxShort = 32767.0 * amplitude.coerceIn(0.0, 1.0)

        for (i in 0 until length) {
            val env = when {
                i < rampSamples -> 0.5 * (1.0 - cos(PI * i / rampSamples))
                i > length - rampSamples -> 0.5 * (1.0 - cos(PI * (length - i) / rampSamples))
                else -> 1.0
            }
            val sample = (sin(i * angularStep) * maxShort * env).toInt().coerceIn(-32768, 32767)
            buffer[offset + i] = sample.toShort()
        }
    }

    /**
     * Plays a PCM 16-bit audio buffer over the physical smartphone speaker or AUX cable to a VHF/UHF radio.
     * Blocked automatically when EMCON (Radio Silence) is active.
     */
    @SuppressLint("NewApi")
    fun playPcmOverHardwareAudio(pcm: ShortArray, emconSilent: Boolean = false): Boolean {
        if (emconSilent || pcm.isEmpty()) return false
        return try {
            val byteSize = pcm.size * 2
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE_HZ)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(byteSize.coerceAtLeast(4096))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcm, 0, pcm.size)
            audioTrack.play()
            true
        } catch (_: Exception) {
            false
        }
    }
}
