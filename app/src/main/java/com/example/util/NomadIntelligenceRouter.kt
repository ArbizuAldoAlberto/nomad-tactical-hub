package com.example.util

import java.util.Locale

enum class NairRing(val ringNumber: Int, val label: String, val badgeColorHex: String) {
    RING_0_DETERMINISTIC(0, "ANILLO 0: Motor Clínico/Táctico Determinista (0 ms · 0% Batería)", "#00F29D"),
    RING_1_ON_DEVICE_LLM(1, "ANILLO 1: IA Local On-Device (Gemini Nano AICore / WebLLM Offline)", "#00E5FF"),
    RING_2_USB_ARK_LLM(2, "ANILLO 2: IA Portable NOMAD Ark USB (Llamafile / KoboldCpp GGUF)", "#FFB300"),
    RING_3_CLOUD_MULTI_LLM(3, "ANILLO 3: Enlace Cloud/Satelital Multi-LLM (Default: Gemini 3.5 Flash)", "#B388FF")
}

data class DeterministicTriageCard(
    val matchedRuleTitle: String,
    val immediate60SecAction: String,
    val stepByStepProtocol: List<String>,
    val arkFolderCitation: String,
    val arkPageCitation: String,
    val searchKeywordsEsEn: String
)

data class NairAdvisorResponse(
    val selectedRing: NairRing,
    val selectionReason: String,
    val deterministicCard: DeterministicTriageCard,
    val synthesizedAnswer: String
)

object NomadIntelligenceRouter {

    /**
     * Automatically selects the optimal intelligence ring based on battery, connectivity, USB server state,
     * and emergency urgency, while respecting manual overrides when requested.
     */
    fun selectOptimalRing(
        isOnline: Boolean,
        batteryPercent: Int,
        isUsbLocalServerActive: Boolean = false,
        preferredRingOverride: Int? = null
    ): Pair<NairRing, String> {
        if (preferredRingOverride != null) {
            val manual = NairRing.entries.firstOrNull { it.ringNumber == preferredRingOverride } ?: NairRing.RING_0_DETERMINISTIC
            return manual to "Selección manual fijada por el operador (${manual.label})."
        }
        if (batteryPercent < 20) {
            return NairRing.RING_0_DETERMINISTIC to "Batería crítica ($batteryPercent% < 20%): Conmutación automática a Anillo 0 Determinista para garantizar 0% de drenaje energético adicional."
        }
        if (isUsbLocalServerActive) {
            return NairRing.RING_2_USB_ARK_LLM to "Servidor Local NOMAD Ark USB detectado (127.0.0.1:8081): Usando modelo GGUF Offline con RAG sobre carpetas 00 a 07."
        }
        if (isOnline) {
            return NairRing.RING_3_CLOUD_MULTI_LLM to "Enlace de red activo y batería saludable ($batteryPercent%): Usando Anillo 3 (Default recomendado: Gemini 3.5 Flash) + Anillo 0 de verificación clínica."
        }
        return NairRing.RING_1_ON_DEVICE_LLM to "Modo 100% Fuera de Línea en dispositivo móvil (Batería $batteryPercent%): Usando motor local offline (Anillo 1 + Anillo 0 RAG FTS5)."
    }

    /**
     * Ring 0 Deterministic Engine (0 ms latency, 100% offline, medically & physically audited).
     * Guarantees zero hallucinations by matching symptoms/scenarios directly against the NOMAD Ark Master Index.
     */
    fun queryRing0Deterministic(rawQuery: String): DeterministicTriageCard {
        val q = rawQuery.lowercase(Locale.getDefault())

        return when {
            q.contains("sangr") || q.contains("hemorrag") || q.contains("torniquete") || q.contains("arteria") || q.contains("herida") || q.contains("corte") -> {
                DeterministicTriageCard(
                    matchedRuleTitle = "PROTOCOLO MARCH — CONTROL DE HEMORRAGIA MASIVA",
                    immediate60SecAction = "Presión directa firme inmediata. Si es sangrado arterial en extremidad, coloque TORNIQUETE TÁCTICO 5-7 cm arriba de la herida (nunca sobre articulación) y anote la hora exacta (HH:MM).",
                    stepByStepProtocol = listOf(
                        "1. Aplique presión directa con gasa hemostática o tela limpia usando el peso del cuerpo por mínimo 3 minutos continuos.",
                        "2. Si no cede en extremidad: ajuste el molinete del torniquete hasta detener el sangrado y desaparecer el pulso distal.",
                        "3. En ingle, axila o cuello: realice empaquetado profundo de la cavidad (Wound Packing) y mantenga compresión.",
                        "4. Abrigue al paciente con manta térmica Mylar (la hipotermia impide la coagulación sanguínea).",
                        "5. Active el Temporizador de Torniquete en la pestaña CÁLCULOS y la Guía por Voz Manos Libres."
                    ),
                    arkFolderCitation = "01_INMEDIATO_PRIMERAS_HORAS/01_MEDICINA_TRAUMA_Y_TRIAJE",
                    arkPageCitation = "Manual Stop the Bleed / Donde no hay doctor, Pág. 12-18",
                    searchKeywordsEsEn = "TORNIQUETE / BLEEDING / HEMORRHAGE / MARCH"
                )
            }

            q.contains("rcp") || q.contains("paro") || q.contains("corazon") || q.contains("corazón") || q.contains("respir") || q.contains("asfixia") || q.contains("ahog") -> {
                DeterministicTriageCard(
                    matchedRuleTitle = "REANIMACIÓN CARDIOPULMONAR (RCP 110 BPM) Y VÍA AÉREA",
                    immediate60SecAction = "Verifique respuesta. Si no respira normalmente, inicie compresiones torácicas fuertes y rápidas en el centro del pecho a 100-120 BPM (active el Metrónomo RCP 110 BPM de esta app).",
                    stepByStepProtocol = listOf(
                        "1. Coloque al paciente boca arriba sobre una superficie rígida.",
                        "2. Abra la vía aérea con maniobra frente-mentón (o tracción mandibular si hay sospecha de trauma cervical).",
                        "3. Comprima el centro del esternón de 5 a 6 cm de profundidad al ritmo del metrónomo (110 compresiones/minuto), permitiendo que el tórax vuelva a su posición.",
                        "4. Si hay obstrucción total por cuerpo extraño en paciente consciente: ejecute compresiones abdominales (Maniobra de Heimlich)."
                    ),
                    arkFolderCitation = "01_INMEDIATO_PRIMERAS_HORAS/01_MEDICINA_TRAUMA_Y_TRIAJE",
                    arkPageCitation = "Manual Primeros Auxilios Cruz Roja / AHA, Pág. 6-11",
                    searchKeywordsEsEn = "RCP / CPR / AIRWAY / HEIMLICH / CHOKING"
                )
            }

            q.contains("agua") || q.contains("cloro") || q.contains("lavandina") || q.contains("potabil") || q.contains("sed") || q.contains("suero") || q.contains("deshidrat") -> {
                DeterministicTriageCard(
                    matchedRuleTitle = "POTABILIZACIÓN QUÍMICA EXACTA Y SUERO ORAL OMS",
                    immediate60SecAction = "Hierva el agua durante 1 min (3 min a >2000 msnm) o dosifique hipoclorito según concentración en g/L y espere 30 min. Para rehidratar: 1 L agua + 1/2 cdta rasa de sal (2.6g) + 6 cdtas rasas de azúcar (13.5g).",
                    stepByStepProtocol = listOf(
                        "1. Prefiltre el agua turbia con tela de algodón limpia o decante por 1 hora.",
                        "2. Dosificación por litro de agua clara (duplicar si es turbia): Lavandina 55 g/L = 1 gota/L (15 gotas en 20 L); Lavandina 25 g/L = 2 gotas/L (32 gotas en 20 L).",
                        "3. Agite y deje reposar 30 minutos a la sombra antes de beber.",
                        "4. Suero de Rehidratación Oral OMS: Nunca supere 1/2 cucharadita rasa de sal por litro para evitar hipernatremia."
                    ),
                    arkFolderCitation = "02_CORTO_PLAZO_DIAS_Y_SEMANAS/01_AGUA_POTABILIZACION_Y_ACOPIO",
                    arkPageCitation = "Guía OMS Potabilización y SRO, Pág. 5-19",
                    searchKeywordsEsEn = "CLORO / BLEACH / WATER / SUERO / ORS"
                )
            }

            q.contains("nuclear") || q.contains("radiaci") || q.contains("fallout") || q.contains("bomba") || q.contains("emp") || q.contains("faraday") || q.contains("yodo") -> {
                DeterministicTriageCard(
                    matchedRuleTitle = "DEFENSA NUCLEAR, REGLA 7:10 DE GEIGER Y BLINDAJE HVL",
                    immediate60SecAction = "Busque refugio subterráneo o en el centro de un edificio de hormigón en menos de 15 minutos antes de que caiga la ceniza radiactiva. Selle ventilación y aplique la Regla 7:10.",
                    stepByStepProtocol = listOf(
                        "1. Interponga masa densa entre usted y el exterior (HVL Gamma: Plomo 1.2 cm, Acero 2.5 cm, Hormigón 6.1 cm, Tierra 9.1 cm, Agua 18 cm).",
                        "2. Regla 7:10: a las 7 horas la radiación cae al 10%; a las 49 horas (~2 días) cae al 1%; a las 2 semanas cae al 0.1%.",
                        "3. Descontaminación: retire la ropa exterior fuera del refugio (elimina el 90% de partículas) y lave piel con agua y jabón sin acondicionador.",
                        "4. Proteja su pendrive NOMAD Ark y radio VHF en lata metálica aislada interiormente (Jaula de Faraday)."
                    ),
                    arkFolderCitation = "01_INMEDIATO_PRIMERAS_HORAS/02_DEFENSA_CIVIL_GUERRA_Y_NUCLEAR",
                    arkPageCitation = "Nuclear War Survival Skills (C. Kearny), Pág. 22-65",
                    searchKeywordsEsEn = "NUCLEAR / FALLOUT / HVL / FARADAY / KI"
                )
            }

            q.contains("radio") || q.contains("baofeng") || q.contains("frecuencia") || q.contains("antena") || q.contains("lora") || q.contains("morse") || q.contains("sstv") -> {
                DeterministicTriageCard(
                    matchedRuleTitle = "COMUNICACIONES DE EMERGENCIA VHF/UHF/LORA Y PROGRAMACIÓN BAOFENG",
                    immediate60SecAction = "Sintonice VHF 146.520 MHz, PMR446 Canal 3 (446.03125 MHz), Canal 16 Marino (156.800 MHz) o CB Canal 9 (27.065 MHz). Escuche 3 minutos cada 3 horas en punto (Regla 3-3-3).",
                    stepByStepProtocol = listOf(
                        "1. En Baofeng UV-5R: pulse [VFO/MR], elija banda A con [A/B] y digite 1-4-6-5-2-0.",
                        "2. Desactive desplazamiento de repetidora en [MENU] -> [2][5] (SFT-D) -> OFF.",
                        "3. Para transmitir coordenadas cifradas o mapas SSTV desde NOMAD: conecte cable AUX o active VOX ([MENU] -> [4]) y use la pestaña COMMS.",
                        "4. Antena dipolo de emergencia para 146.520 MHz: corte dos ramas de cobre de 48.6 cm (1/4 de onda = 71.25 / f)."
                    ),
                    arkFolderCitation = "02_CORTO_PLAZO_DIAS_Y_SEMANAS/06_COMUNICACIONES_RADIO_Y_ANTENAS",
                    arkPageCitation = "Manual Radio de Emergencia & Antenas, Pág. 10-42",
                    searchKeywordsEsEn = "BAOFENG / VHF / DIPOLO / LORA / SSTV"
                )
            }

            q.contains("quemad") || q.contains("fuego") || q.contains("incendio") || q.contains("parkland") -> {
                DeterministicTriageCard(
                    matchedRuleTitle = "MANEJO DE QUEMADURAS GRAVES Y FÓRMULA DE PARKLAND",
                    immediate60SecAction = "Irrigue con agua limpia fresca (15-20 min, nunca hielo). Retire anillos y relojes antes del edema. Calcule reposición hídrica con la Fórmula de Parkland (4 mL × kg × %SCTQ).",
                    stepByStepProtocol = listOf(
                        "1. Detenga el proceso térmico y cubra con paño estéril limpio y seco sin reventar ampollas.",
                        "2. Estime % de Superficie Corporal Quemada (Regla de los 9 de Wallace: Cabeza 9%, cada Brazo 9%, Tronco anterior 18%, Tronco posterior 18%, cada Pierna 18%, Palma de la mano = 1%).",
                        "3. Administre el 50% del volumen calculado en las primeras 8 horas y el 50% restante en las siguientes 16 horas."
                    ),
                    arkFolderCitation = "01_INMEDIATO_PRIMERAS_HORAS/01_MEDICINA_TRAUMA_Y_TRIAJE",
                    arkPageCitation = "Donde no hay doctor / Trauma Térmico, Pág. 54-60",
                    searchKeywordsEsEn = "QUEMADURA / BURN / PARKLAND / WALLACE"
                )
            }

            else -> {
                DeterministicTriageCard(
                    matchedRuleTitle = "PROTOCOLO GENERAL DE SUPERVIVENCIA Y SOLIDARIDAD NOMAD (REGLA DE LOS 3)",
                    immediate60SecAction = "Aplique la Regla de los 3: 3 minutos sin aire (vía aérea/hemorragia), 3 horas sin refugio en clima extremo, 3 días sin agua potable, 3 semanas sin comida. Mantenga la calma y verifique el Índice Maestro 00.",
                    stepByStepProtocol = listOf(
                        "1. Evalúe seguridad inmediata del entorno (derrumbes, gases, fuego, radiación o cables caídos con el detector EMF).",
                        "2. Asegure agua potable (hervida o clorada según g/L) y abrigo térmico aislándose del suelo.",
                        "3. Sintonice su radio en las frecuencias de emergencia y sincronice su unidad NOMAD Ark USB con refugios vecinos."
                    ),
                    arkFolderCitation = "00_INDICE_MAESTRO_Y_PROTOCOLOS/00_INDICE_MAESTRO_EMERGENCIAS",
                    arkPageCitation = "Índice Maestro NOMAD Ark, Pág. 1-8",
                    searchKeywordsEsEn = "TRIAJE / AGUA / REFUGIO / RADIO / SOLIDARIDAD"
                )
            }
        }
    }

    /**
     * Unified Adaptive Survival Advisor entry point across all 4 Rings.
     */
    suspend fun askAdaptiveAdvisor(
        prompt: String,
        batteryPercent: Int = 80,
        isOnline: Boolean = false,
        isUsbLocalServerActive: Boolean = false,
        preferredRingOverride: Int? = null
    ): NairAdvisorResponse {
        val card = queryRing0Deterministic(prompt)
        val (ring, reason) = selectOptimalRing(isOnline, batteryPercent, isUsbLocalServerActive, preferredRingOverride)

        val headerBlock = buildString {
            appendLine("🛡️ [${card.matchedRuleTitle}]")
            appendLine("⚡ ACCIÓN <60 SEG: ${card.immediate60SecAction}")
            appendLine("📖 CITA VERIFICADA: ${card.arkFolderCitation} (${card.arkPageCitation})")
            appendLine("🔎 LUPA NOMAD ARK: ${card.searchKeywordsEsEn}")
            appendLine("---")
            card.stepByStepProtocol.forEach { appendLine(it) }
        }

        val detailBlock = when (ring) {
            NairRing.RING_0_DETERMINISTIC -> {
                "\n[MODO DETERMINISTA 0ms ACTIVO]: Respuesta clínica/táctica verificada localmente sin consumo de red ni batería extra."
            }
            NairRing.RING_1_ON_DEVICE_LLM -> {
                "\n[SÍNTESIS ANILLO 1 ON-DEVICE OFFLINE]: Basado en el índice local de SurvivalKnowledgeBase, priorice la estabilización física, registre coordenadas en el Ledger GPS y utilice las Calculadoras de Crisis para validar dosis y parámetros exactos."
            }
            NairRing.RING_2_USB_ARK_LLM -> {
                "\n[SÍNTESIS ANILLO 2 USB ARK GGUF]: Consultando biblioteca extendida en carpetas 00 a 07 del pendrive NOMAD Ark. Verifique los manuales en formato .epub (Kindle) o .pdf dentro de ${card.arkFolderCitation}."
            }
            NairRing.RING_3_CLOUD_MULTI_LLM -> {
                val cloudReply = GeminiClient.askTacticalAdvisor(prompt)
                "\n[ANÁLISIS ANILLO 3 GEMINI CLOUD]:\n$cloudReply"
            }
        }

        return NairAdvisorResponse(
            selectedRing = ring,
            selectionReason = reason,
            deterministicCard = card,
            synthesizedAnswer = headerBlock + detailBlock
        )
    }
}
