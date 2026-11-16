package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SurvivalViewModel(
    private val repository: SurvivalRepository,
    val sensorBridge: HardwareSensorBridge? = null
) : ViewModel() {

    // Tab State
    val currentTab = MutableStateFlow("panel") // "panel", "guias", "unidad", "crisis", "mapa", "cifrado", "inventario"

    // Global Tactical Modes (OMEGA Shield)
    val emconSilentMode = MutableStateFlow(false) // Radio Silence (Blocks RF, Audio TX, Torch, Cloud)
    val traumaLargeUiMode = MutableStateFlow(false) // Giant buttons / High-contrast Trauma Mode
    val oledBlackoutMode = MutableStateFlow(false) // Pure #000000 battery-saving mode
    val pinAccessMode = MutableStateFlow(PinAccessMode.NORMAL) // NORMAL vs DURESS_COERCION

    // Raw Repository Flows + Duress PIN Coercion Filtering
    private val rawWaypoints: StateFlow<List<SurvivalWaypoint>> = repository.allWaypoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waypoints: StateFlow<List<SurvivalWaypoint>> = combine(rawWaypoints, pinAccessMode) { list, mode ->
        if (mode == PinAccessMode.DURESS_COERCION) {
            listOf(
                SurvivalWaypoint(
                    id = 999,
                    name = "Punto Público Municipal",
                    type = "Refugio",
                    latitude = -36.2300,
                    longitude = -61.1100,
                    description = "Centro de asistencia civil estándar."
                )
            )
        } else {
            list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val rawInventoryItems: StateFlow<List<InventoryItem>> = repository.allInventoryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryItems: StateFlow<List<InventoryItem>> = combine(rawInventoryItems, pinAccessMode) { list, mode ->
        if (mode == PinAccessMode.DURESS_COERCION) {
            list.take(2).map { it.copy(quantity = 1) }
        } else {
            list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transmissions: StateFlow<List<TransmissionRecord>> = repository.allTransmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tourniquetTimers: StateFlow<List<TourniquetTimerEntity>> = repository.allTourniquetTimers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 360° Physical Sensor Telemetry (Centered on San Carlos de Bolívar, Bs. As., Argentina)
    val currentLat = MutableStateFlow(-36.2333)
    val currentLng = MutableStateFlow(-61.1167)
    val compassDegree = MutableStateFlow(0f)
    val barometerHpa = MutableStateFlow(1013.25f)
    val magneticFieldUt = MutableStateFlow(48.5f)
    val emfAnomalyAlert = MutableStateFlow(false)
    val stormWarningAlert = MutableStateFlow(false)
    val batteryLevelPercent = MutableStateFlow(85)
    val isCprMetronomeRunning = MutableStateFlow(false)
    val cprBeatCounter = MutableStateFlow(0)

    // Morse & Radio Signal Engine States
    val morseInputText = MutableStateFlow("")
    val morseOutputText = MutableStateFlow("")

    val textToMorseInput = MutableStateFlow("")
    val textToMorseOutput = MutableStateFlow("")

    val morseLightOn = MutableStateFlow(false)
    val isFlashingCurrently = MutableStateFlow(false)
    val radioAudioTxEnabled = MutableStateFlow(true) // 800Hz CW + AFSK / SSTV over speaker/AUX
    val lastRadioTelemetryStatus = MutableStateFlow("Módem Acústico 800Hz / AFSK 1200 / SSTV Robot36 Listo")
    private var morseFlashJob: Job? = null

    // Cipher States (Vigenère, AES-256-GCM, One-Time Pad)
    val selectedCipherProtocol = MutableStateFlow("AES-256-GCM") // "AES-256-GCM", "Vigenère", "One-Time Pad"
    val cipherKey = MutableStateFlow("NOMAD")
    val cipherTextInput = MutableStateFlow("")
    val cipherTextOutput = MutableStateFlow("")

    // NAIR 4-Ring Adaptive Intelligence Advisor States
    val preferredNairRing = MutableStateFlow<Int?>(null) // null = Auto-Recommend Optimal Ring
    val activeNairRingLabel = MutableStateFlow(NairRing.RING_0_DETERMINISTIC.label)
    val activeNairReason = MutableStateFlow("Selección automática inteligente según batería y red.")
    val advisorPrompt = MutableStateFlow("")
    val advisorResponse = MutableStateFlow("")
    val isAdvisorLoading = MutableStateFlow(false)

    // Cold-Sync (.nomadpkg) USB OTG States
    val coldSyncPayloadText = MutableStateFlow("")
    val coldSyncStatusMessage = MutableStateFlow("")

    // Waypoint Addition state (Bolívar default)
    val addWpName = MutableStateFlow("")
    val addWpType = MutableStateFlow("Refugio")
    val addWpLat = MutableStateFlow("-36.2333")
    val addWpLng = MutableStateFlow("-61.1167")
    val addWpDesc = MutableStateFlow("")
    val isWpFormOpen = MutableStateFlow(false)

    // Inventory Addition state
    val addInvName = MutableStateFlow("")
    val addInvCategory = MutableStateFlow("Mochila (BOB)")
    val addInvQty = MutableStateFlow("1")
    val isInvFormOpen = MutableStateFlow(false)

    // --- SOLIDARITY & HUMANITY STATES (Backed by Room SQLite + Web of Trust Ed25519/HMAC) ---
    val pactoEmpatia = MutableStateFlow(false)
    val pactoApoyo = MutableStateFlow(false)
    val pactoInformacion = MutableStateFlow(false)
    val pactoComunicaciones = MutableStateFlow(false)
    val pactoVulnerables = MutableStateFlow(false)

    val solidarityPosts: StateFlow<List<SolidarityPost>> = repository.allSolidarityPosts
        .map { entities ->
            entities.map { e ->
                SolidarityPost(
                    id = e.id,
                    author = e.authorCallsign,
                    message = e.message,
                    type = e.category,
                    region = e.region,
                    upvotes = e.upvotes,
                    signatureHex = e.signatureHex,
                    verifiedWebOfTrust = e.verifiedWebOfTrust,
                    timestamp = e.timestamp
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newPostAuthor = MutableStateFlow("")
    val newPostMessage = MutableStateFlow("")
    val newPostType = MutableStateFlow("Mensaje de Esperanza")
    val newPostRegion = MutableStateFlow("")

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }

        // Bind live physical sensor telemetry from HardwareSensorBridge when available
        sensorBridge?.let { bridge ->
            viewModelScope.launch { bridge.latitude.collect { currentLat.value = it } }
            viewModelScope.launch { bridge.longitude.collect { currentLng.value = it } }
            viewModelScope.launch { bridge.compassHeadingDeg.collect { compassDegree.value = it } }
            viewModelScope.launch { bridge.pressureHpa.collect { barometerHpa.value = it } }
            viewModelScope.launch { bridge.magneticFieldMicroTesla.collect { magneticFieldUt.value = it } }
            viewModelScope.launch { bridge.emfAnomalyAlert.collect { emfAnomalyAlert.value = it } }
            viewModelScope.launch { bridge.barometerStormWarning.collect { stormWarningAlert.value = it } }
            viewModelScope.launch { bridge.batteryPercent.collect { batteryLevelPercent.value = it } }
            viewModelScope.launch { bridge.isCprMetronomeRunning.collect { isCprMetronomeRunning.value = it } }
            viewModelScope.launch { bridge.cprBeatCount.collect { cprBeatCounter.value = it } }
        }
    }

    // Security & Tactical Mode Controls
    fun unlockWithPin(pinInput: String): PinAccessMode {
        val mode = SurvivalCipher.evaluatePinAccess(pinInput)
        if (mode != PinAccessMode.DENIED) {
            pinAccessMode.value = mode
        }
        return mode
    }

    fun toggleEmconSilence(enabled: Boolean) {
        emconSilentMode.value = enabled
        if (enabled) {
            stopMorseFlashing()
            sensorBridge?.toggleCprMetronome(false, true)
            lastRadioTelemetryStatus.value = "SILENCIO DE RADIO (EMCON) ACTIVO: Cero emisiones RF/Acústicas/Ópticas."
        } else {
            lastRadioTelemetryStatus.value = "EMCON Desactivado: Transceptores listos."
        }
    }

    fun toggleCprMetronome110Bpm() {
        val next = !isCprMetronomeRunning.value
        sensorBridge?.toggleCprMetronome(next, emconSilentMode.value)
        if (sensorBridge == null) {
            isCprMetronomeRunning.value = next
        }
    }

    fun speakProtocolHandsFree(text: String) {
        sensorBridge?.speakHandsFree(text, emconSilentMode.value)
    }

    // Tourniquet Timer Actions
    fun addTourniquetTimer(patientTag: String, limbLocation: String) {
        viewModelScope.launch {
            repository.insertTourniquetTimer(
                TourniquetTimerEntity(
                    patientTag = patientTag.ifBlank { "Herido #1" },
                    limbLocation = limbLocation.ifBlank { "Extremidad" }
                )
            )
        }
    }

    fun deleteTourniquetTimer(timer: TourniquetTimerEntity) {
        viewModelScope.launch {
            repository.deleteTourniquetTimer(timer)
        }
    }

    // Waypoint Actions
    fun createWaypoint() {
        val latVal = addWpLat.value.replace(',', '.').toDoubleOrNull() ?: currentLat.value
        val lngVal = addWpLng.value.replace(',', '.').toDoubleOrNull() ?: currentLng.value
        val nameVal = addWpName.value.ifBlank { "Waypoint Táctico" }
        val descVal = addWpDesc.value.ifBlank { "Punto de interés guardado localmente." }

        viewModelScope.launch {
            repository.insertWaypoint(
                SurvivalWaypoint(
                    name = nameVal,
                    type = addWpType.value,
                    latitude = latVal,
                    longitude = lngVal,
                    description = descVal
                )
            )
            addWpName.value = ""
            addWpDesc.value = ""
            isWpFormOpen.value = false
        }
    }

    fun removeWaypoint(wp: SurvivalWaypoint) {
        viewModelScope.launch {
            repository.deleteWaypoint(wp)
        }
    }

    // Inventory Actions
    fun addInventoryItem() {
        val qtyVal = addInvQty.value.toIntOrNull() ?: 1
        val nameVal = addInvName.value.ifBlank { "Suministro nuevo" }

        viewModelScope.launch {
            repository.insertInventoryItem(
                InventoryItem(
                    name = nameVal,
                    category = addInvCategory.value,
                    quantity = qtyVal,
                    isPacked = false
                )
            )
            addInvName.value = ""
            addInvQty.value = "1"
            isInvFormOpen.value = false
        }
    }

    fun toggleItemPacked(item: InventoryItem) {
        viewModelScope.launch {
            repository.updateInventoryItem(item.copy(isPacked = !item.isPacked))
        }
    }

    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    // Cryptographic & Radio Packet Actions
    fun transmitEncryptedMessage() {
        val raw = cipherTextInput.value
        val key = cipherKey.value
        if (raw.isBlank()) return

        val protocol = selectedCipherProtocol.value
        val cipher = when (protocol) {
            "AES-256-GCM" -> SurvivalCipher.encryptAesGcm(raw, key)
            "One-Time Pad" -> SurvivalCipher.encryptOneTimePad(raw, key)
            else -> SurvivalCipher.encryptVigenere(raw, key)
        }
        cipherTextOutput.value = cipher

        viewModelScope.launch(Dispatchers.Default) {
            repository.insertTransmission(
                TransmissionRecord(
                    rawText = raw,
                    cipherText = cipher,
                    cipherKey = key,
                    protocol = protocol,
                    isIncoming = false
                )
            )
            if (radioAudioTxEnabled.value && !emconSilentMode.value) {
                val neppBytes = NomadSignalEngine.encodeNeppPacket(
                    type = NeppMessageType.SOLIDARITY_AID,
                    senderCallsign = "NOMAD-ALPHA",
                    latitude = currentLat.value,
                    longitude = currentLng.value,
                    payloadText = raw
                )
                val afskPcm = NomadSignalEngine.generateAfskPacketPcm(neppBytes)
                NomadSignalEngine.playPcmOverHardwareAudio(afskPcm, emconSilentMode.value)
                lastRadioTelemetryStatus.value = "Trama NEPP (${neppBytes.size} B + FEC) modulada en AFSK 1200/2200Hz."
            }
        }
    }

    fun transmitSstvCoordinateBeacon() {
        if (emconSilentMode.value) {
            lastRadioTelemetryStatus.value = "Bloqueado por EMCON (Silencio de Radio)."
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            val sstvPcm = NomadSignalEngine.generateSstvTacticalBeaconPcm()
            NomadSignalEngine.playPcmOverHardwareAudio(sstvPcm, emconSilentMode.value)
            lastRadioTelemetryStatus.value = "Baliza visual SSTV Robot36 emitida en audio (${currentLat.value}, ${currentLng.value})."
        }
    }

    fun decryptIncomingMessage(cipher: String) {
        val key = cipherKey.value
        if (cipher.isBlank()) return

        val plain = when (selectedCipherProtocol.value) {
            "AES-256-GCM" -> SurvivalCipher.decryptAesGcm(cipher, key)
            "One-Time Pad" -> SurvivalCipher.decryptOneTimePad(cipher, key)
            else -> SurvivalCipher.decryptVigenere(cipher, key)
        }
        cipherTextInput.value = plain
        cipherTextOutput.value = cipher
    }

    fun deleteTransmissionRecord(record: TransmissionRecord) {
        viewModelScope.launch {
            repository.deleteTransmission(record)
        }
    }

    // Morse Controls (Real Camera Torch + 800Hz CW Audio)
    fun onTranslateTextToMorse(text: String) {
        textToMorseInput.value = text
        textToMorseOutput.value = SurvivalCipher.textToMorse(text)
    }

    fun onTranslateMorseToText(morse: String) {
        morseInputText.value = morse
        morseOutputText.value = SurvivalCipher.morseToText(morse)
    }

    fun startMorseFlashing(morsePattern: String) {
        morseFlashJob?.cancel()
        if (morsePattern.isBlank() || emconSilentMode.value) return

        isFlashingCurrently.value = true
        morseFlashJob = viewModelScope.launch(Dispatchers.Default) {
            if (radioAudioTxEnabled.value) {
                val cwPcm = NomadSignalEngine.generateMorseAudioPcm(morsePattern, includeVoxPreamble = true)
                NomadSignalEngine.playPcmOverHardwareAudio(cwPcm, emconSilentMode.value)
            }
            val timings = SurvivalCipher.getMorseTimingPattern(morsePattern)
            for (step in timings) {
                morseLightOn.value = step.first
                sensorBridge?.setTorchLight(step.first, emconSilentMode.value)
                delay(step.second)
            }
            morseLightOn.value = false
            sensorBridge?.setTorchLight(false, emconSilentMode.value)
            isFlashingCurrently.value = false
        }
    }

    fun stopMorseFlashing() {
        morseFlashJob?.cancel()
        morseLightOn.value = false
        sensorBridge?.setTorchLight(false, emconSilentMode.value)
        isFlashingCurrently.value = false
    }

    // NAIR 4-Ring Adaptive Tactical Advisor
    fun askAdvisor() {
        val prompt = advisorPrompt.value
        if (prompt.isBlank()) return

        isAdvisorLoading.value = true
        viewModelScope.launch {
            val battery = sensorBridge?.refreshBatteryLevel() ?: batteryLevelPercent.value
            val onlineAllowed = !emconSilentMode.value
            val nairResult = NomadIntelligenceRouter.askAdaptiveAdvisor(
                prompt = prompt,
                batteryPercent = battery,
                isOnline = onlineAllowed,
                isUsbLocalServerActive = false,
                preferredRingOverride = preferredNairRing.value
            )
            activeNairRingLabel.value = nairResult.selectedRing.label
            activeNairReason.value = nairResult.selectionReason
            advisorResponse.value = nairResult.synthesizedAnswer
            isAdvisorLoading.value = false
        }
    }

    fun quickAsk(topic: String) {
        advisorPrompt.value = topic
        askAdvisor()
    }

    // Solidarity & Web-of-Trust Actions
    fun publishSolidarityPost() {
        val author = newPostAuthor.value.ifBlank { "Ciudadano Nomad (Cono Sur)" }
        val msgText = newPostMessage.value
        val typeVal = newPostType.value
        val regVal = newPostRegion.value.ifBlank { "Buenos Aires / Cono Sur" }

        if (msgText.isBlank()) return

        val sig = SurvivalCipher.signWebOfTrustMessage(author, msgText, cipherKey.value)
        viewModelScope.launch {
            repository.insertSolidarityPost(
                SolidarityPostEntity(
                    authorCallsign = author,
                    message = msgText,
                    category = typeVal,
                    region = regVal,
                    latitude = currentLat.value,
                    longitude = currentLng.value,
                    upvotes = 1,
                    signatureHex = sig,
                    verifiedWebOfTrust = true
                )
            )
            newPostAuthor.value = ""
            newPostMessage.value = ""
            newPostRegion.value = ""
        }
    }

    fun upvotePost(post: SolidarityPost) {
        viewModelScope.launch {
            repository.updateSolidarityPost(
                SolidarityPostEntity(
                    id = post.id,
                    authorCallsign = post.author,
                    message = post.message,
                    category = post.type,
                    region = post.region,
                    upvotes = post.upvotes + 1,
                    signatureHex = post.signatureHex,
                    verifiedWebOfTrust = post.verifiedWebOfTrust,
                    timestamp = post.timestamp
                )
            )
        }
    }

    // USB-C OTG Cold-Sync (.nomadpkg) Actions
    fun generateColdSyncExport() {
        viewModelScope.launch {
            val pkg = repository.exportColdSyncPackage("NOMAD-BOLIVAR-01", cipherKey.value)
            coldSyncPayloadText.value = pkg
            coldSyncStatusMessage.value = "Paquete .nomadpkg firmado generado con éxito (SHA-256 + HmacSHA256)."
        }
    }

    fun importColdSyncPayload(jsonPayload: String) {
        viewModelScope.launch {
            val res = repository.importColdSyncPackage(jsonPayload, cipherKey.value)
            coldSyncStatusMessage.value = res.summary
        }
    }
}

data class SolidarityPost(
    val id: Int = 0,
    val author: String,
    val message: String,
    val type: String,
    val region: String,
    val upvotes: Int,
    val signatureHex: String = "",
    val verifiedWebOfTrust: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
