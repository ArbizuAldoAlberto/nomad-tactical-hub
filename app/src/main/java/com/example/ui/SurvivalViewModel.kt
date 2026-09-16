package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.GeminiClient
import com.example.util.SurvivalCipher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SurvivalViewModel(private val repository: SurvivalRepository) : ViewModel() {

    // Tab State
    val currentTab = MutableStateFlow("panel") // "panel", "guias", "mapa", "cifrado", "inventario", "repos"

    // Repository Flows
    val waypoints: StateFlow<List<SurvivalWaypoint>> = repository.allWaypoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryItems: StateFlow<List<InventoryItem>> = repository.allInventoryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transmissions: StateFlow<List<TransmissionRecord>> = repository.allTransmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Simulated GPS coords
    val currentLat = MutableStateFlow(40.7128)
    val currentLng = MutableStateFlow(-74.0060)
    val compassDegree = MutableStateFlow(0f)

    // Morse Translation States
    val morseInputText = MutableStateFlow("")
    val morseOutputText = MutableStateFlow("")
    
    val textToMorseInput = MutableStateFlow("")
    val textToMorseOutput = MutableStateFlow("")

    val morseLightOn = MutableStateFlow(false)
    val isFlashingCurrently = MutableStateFlow(false)
    private var morseFlashJob: Job? = null

    // Vigenere Cipher States
    val cipherKey = MutableStateFlow("NOMAD")
    val cipherTextInput = MutableStateFlow("")
    val cipherTextOutput = MutableStateFlow("")

    // AI Tactical Advisor States
    val advisorPrompt = MutableStateFlow("")
    val advisorResponse = MutableStateFlow("")
    val isAdvisorLoading = MutableStateFlow(false)

    // Waypoint Addition state
    val addWpName = MutableStateFlow("")
    val addWpType = MutableStateFlow("Refugio") // "Refugio", "Pozo de Agua", "Suministros", "Punto de Extracción", "Peligro"
    val addWpLat = MutableStateFlow("40.7128")
    val addWpLng = MutableStateFlow("-74.0060")
    val addWpDesc = MutableStateFlow("")
    val isWpFormOpen = MutableStateFlow(false)

    // Inventory Addition state
    val addInvName = MutableStateFlow("")
    val addInvCategory = MutableStateFlow("Mochila (BOB)") // "Mochila (BOB)", "Alimentos/Despensa", "Primeros Auxilios", "Herramientas/Comms"
    val addInvQty = MutableStateFlow("1")
    val isInvFormOpen = MutableStateFlow(false)

    init {
        // Prepopulate db if empty
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }

        // Randomize some compass orientation adjustments for retro feel
        viewModelScope.launch {
            while (true) {
                delay(1200)
                val current = compassDegree.value
                val offset = (-3..3).random()
                compassDegree.value = (current + offset + 360f) % 360f
            }
        }
    }

    // Waypoint Actions
    fun createWaypoint() {
        val latVal = addWpLat.value.replace(',', '.').toDoubleOrNull() ?: currentLat.value
        val lngVal = addWpLng.value.replace(',', '.').toDoubleOrNull() ?: currentLng.value
        val nameVal = addWpName.value.ifBlank { "Waypoint Táctico" }
        val descVal = addWpDesc.value.ifBlank { "Un punto de interés guardado localmente." }

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
            // Reset
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
            // Reset
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

    // Transmission Actions
    fun transmitEncryptedMessage() {
        val raw = cipherTextInput.value
        val key = cipherKey.value
        if (raw.isBlank()) return

        val cipher = SurvivalCipher.encryptVigenere(raw, key)
        cipherTextOutput.value = cipher

        viewModelScope.launch {
            repository.insertTransmission(
                TransmissionRecord(
                    rawText = raw,
                    cipherText = cipher,
                    cipherKey = key,
                    protocol = "AES-Nomad (Vigenère)",
                    isIncoming = false
                )
            )
        }
    }

    fun decryptIncomingMessage(cipher: String) {
        val key = cipherKey.value
        if (cipher.isBlank()) return

        val plain = SurvivalCipher.decryptVigenere(cipher, key)
        cipherTextInput.value = plain
        cipherTextOutput.value = cipher
    }

    fun deleteTransmissionRecord(record: TransmissionRecord) {
        viewModelScope.launch {
            repository.deleteTransmission(record)
        }
    }

    // Morse Controls
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
        if (morsePattern.isBlank()) return

        isFlashingCurrently.value = true
        morseFlashJob = viewModelScope.launch(Dispatchers.Default) {
            val timings = SurvivalCipher.getMorseTimingPattern(morsePattern)
            for (step in timings) {
                morseLightOn.value = step.first
                delay(step.second)
            }
            morseLightOn.value = false
            isFlashingCurrently.value = false
        }
    }

    fun stopMorseFlashing() {
        morseFlashJob?.cancel()
        morseLightOn.value = false
        isFlashingCurrently.value = false
    }

    // Tactical Advisor API
    fun askAdvisor() {
        val prompt = advisorPrompt.value
        if (prompt.isBlank()) return

        isAdvisorLoading.value = true
        viewModelScope.launch {
            val answer = GeminiClient.askTacticalAdvisor(prompt)
            advisorResponse.value = answer
            isAdvisorLoading.value = false
        }
    }

    fun quickAsk(topic: String) {
        advisorPrompt.value = topic
        askAdvisor()
    }

    // --- SOLIDARITY & HUMANITY STATES ---
    val pactoEmpatia = MutableStateFlow(false)
    val pactoApoyo = MutableStateFlow(false)
    val pactoInformacion = MutableStateFlow(false)
    val pactoComunicaciones = MutableStateFlow(false)
    val pactoVulnerables = MutableStateFlow(false)

    val solidarityPosts = MutableStateFlow<List<SolidarityPost>>(
        listOf(
            SolidarityPost(
                author = "Comunidad La Esperanza (Santiago)",
                message = "Compartiendo pozo de agua potable filtrada por gravedad. Traigan sus bidones, sin costo. Unidas las familias venceremos.",
                type = "Ayuda Colectiva",
                region = "Región Metropolitana",
                upvotes = 34
            ),
            SolidarityPost(
                author = "Nodo Radiofónico Biobío",
                message = "Transmitiendo en VHF 146.520 MHz manuales de primeros auxilios y medicina silvestre generados con Kiwix. Mantengan la calma.",
                type = "Conocimiento Compartido",
                region = "Biobío",
                upvotes = 27
            ),
            SolidarityPost(
                author = "Madres de la Resistencia (Bogotá)",
                message = "Comedor comunitario activo. Nos faltan verduras, pero nos sobra sopa y empatía. Vengan con sus niños.",
                type = "Ayuda Colectiva",
                region = "Cundinamarca",
                upvotes = 42
            ),
            SolidarityPost(
                author = "Red de Enlace Nomad (Mendoza)",
                message = "Montaje de antena dipolo completado para banda UHF. Conectados con Chile a través de la Cordillera. La humanidad no se detiene.",
                type = "Mensaje de Esperanza",
                region = "Mendoza",
                upvotes = 51
            ),
            SolidarityPost(
                author = "Misión Médica Valparaíso",
                message = "Médicos jubilados atendiendo urgencias menores en el refugio de la iglesia. Medicamentos alternativos y curaciones.",
                type = "Alerta Sanitaria",
                region = "Valparaíso",
                upvotes = 19
            )
        )
    )

    val newPostAuthor = MutableStateFlow("")
    val newPostMessage = MutableStateFlow("")
    val newPostType = MutableStateFlow("Mensaje de Esperanza")
    val newPostRegion = MutableStateFlow("")

    fun publishSolidarityPost() {
        val author = newPostAuthor.value.ifBlank { "Ciudadano Nomad Desconocido" }
        val msgText = newPostMessage.value
        val typeVal = newPostType.value
        val regVal = newPostRegion.value.ifBlank { "Global" }

        if (msgText.isBlank()) return

        val newPost = SolidarityPost(author = author, message = msgText, type = typeVal, region = regVal, upvotes = 1)
        solidarityPosts.value = listOf(newPost) + solidarityPosts.value

        // Reset
        newPostAuthor.value = ""
        newPostMessage.value = ""
        newPostRegion.value = ""
    }

    fun upvotePost(post: SolidarityPost) {
        solidarityPosts.value = solidarityPosts.value.map {
            if (it == post) it.copy(upvotes = it.upvotes + 1) else it
        }
    }
}

data class SolidarityPost(
    val author: String,
    val message: String,
    val type: String, // "Ayuda Colectiva", "Mensaje de Esperanza", "Alerta Sanitaria", "Conocimiento Compartido"
    val region: String,
    val upvotes: Int,
    val timestamp: Long = System.currentTimeMillis()
)
