package com.example.util

data class GuideItem(
    val title: String,
    val iconName: String,
    val category: String,
    val content: String,
    val tips: List<String>,
    val arkFolderRoute: String = "00_INDICE_MAESTRO_Y_PROTOCOLOS",
    val arkPageCitation: String = "Pág. 1-4"
)

data class PictogramPhrase(
    val code: String,
    val iconSymbol: String,
    val es: String,
    val en: String,
    val pt: String,
    val fr: String,
    val zh: String,
    val gn: String // Guaraní / Cono Sur
)

data class RepoAnalysis(
    val name: String,
    val level: String,
    val difficultyColor: String,
    val estimatedCost: String,
    val materials: List<String>,
    val tools: List<String>,
    val description: String,
    val fieldUtility: String,
    val setupSteps: List<String>
)

object SurvivalKnowledgeBase {

    val universalMedicalPictograms = listOf(
        PictogramPhrase(
            code = "MED-01",
            iconSymbol = "🩸",
            es = "¿Dónde tiene dolor o sangrado? Señale con la mano.",
            en = "Where is the pain or bleeding? Point with your hand.",
            pt = "Onde está a dor ou sangramento? Aponte com a mão.",
            fr = "Où avez-vous mal ou saignez-vous ? Montrez avec la main.",
            zh = "哪里疼痛或流血？请用手指出。",
            gn = "Moõpa hasy térã huguy? Ehechauka nde pópe."
        ),
        PictogramPhrase(
            code = "MED-02",
            iconSymbol = "💊",
            es = "¿Es alérgico a algún medicamento (Penicilina) o padece diabetes/asma?",
            en = "Are you allergic to any medicine (Penicillin) or have diabetes/asthma?",
            pt = "Tem alergia a algum remédio (Penicilina) ou tem diabetes/asma?",
            fr = "Êtes-vous allergique à un médicament (Pénicilline) ou diabétique ?",
            zh = "您对任何药物（青霉素）过敏或患有糖尿病/哮喘吗？",
            gn = "Rejapo vai piko pohã (Penicilina) térã reguereko asma/juky rykue?"
        ),
        PictogramPhrase(
            code = "MED-03",
            iconSymbol = "💧",
            es = "Agua potable segura y Suero de Rehidratación Oral. Beba despacio.",
            en = "Safe drinking water and Oral Rehydration Salts. Drink slowly.",
            pt = "Água potável segura e Soro de Reidratação Oral. Beba devagar.",
            fr = "Eau potable sûre et sels de réhydratation orale. Buvez lentement.",
            zh = "安全饮用水和口服补液盐。请慢慢喝。",
            gn = "Yreipotĩ ha pohã juky/asuka. E'u mbeguekatu."
        ),
        PictogramPhrase(
            code = "MED-04",
            iconSymbol = "🛡️",
            es = "Somos socorristas humanitarios. Está a salvo en este refugio.",
            en = "We are humanitarian responders. You are safe in this shelter.",
            pt = "Somos socorristas humanitários. Você está seguro neste abrigo.",
            fr = "Nous sommes des secouristes humanitaires. Vous êtes en sécurité.",
            zh = "我们是人道主义救援人员。您在这个避难所很安全。",
            gn = "Ore pytyvõhára. Reime porã ko óga mo'ãhápe."
        )
    )

    val prepperReposList = listOf(
        RepoAnalysis(
            name = "Project Nomad (Crosstalk Solutions)",
            level = "Avanzado / Táctico (Hardware + Software)",
            difficultyColor = "Red",
            estimatedCost = "$180 - $450 USD",
            materials = listOf(
                "Computador de placa reducida (Raspberry Pi 4/5 4GB o RockPi SBC)",
                "Nodo de radio LoRa Meshtastic (Heltec V3 o LILYGO T-Beam)",
                "Antena para exteriores de 5.8 dBi (sintonizada a 915/868 MHz)",
                "Gabinete hermético grado militar IP67 / Pelican Case",
                "Controlador solar de carga robusto de 5V y batería LiFePO4 de 12V 10Ah",
                "Módulo GPS USB U-blox de bajo consumo para telemetría autónoma"
            ),
            tools = listOf(
                "Soldadora electrónica portátil de estaño",
                "Software BalenaEtcher de grabado flash de firmware",
                "Aplicación móvil NOMAD con cifrado AES-256-GCM"
            ),
            description = "Ecosistema de comunicaciones y cómputo táctico móvil diseñado para ofrecer transmisiones de largo alcance y en malla descentralizada sin conexión terrestre.",
            fieldUtility = "Interconecta terminales móviles con mapas locales fuera de línea, permitiendo el despliegue de balizas de salvamento, geolocalización continua por GPS y mensajería encriptada regional por radiofrecuencia.",
            setupSteps = listOf(
                "1. Flashear el firmware oficial de Meshtastic en el chip LoRa de tu placa.",
                "2. Ensamblar e impermeabilizar la Raspberry Pi y el cargador solar en el maletín de campaña.",
                "3. Conectar el receptor GPS interno y fijar el cable de extensión hacia la varilla de antena coaxial.",
                "4. Enlazar este dispositivo Android al punto de acceso local Wi-Fi / Bluetooth del chasis Nomad."
            )
        ),
        RepoAnalysis(
            name = "Meshtastic (Mesh Decentralized Networking)",
            level = "Intermedio / Táctico",
            difficultyColor = "Cyan",
            estimatedCost = "$25 - $70 USD",
            materials = listOf(
                "Módulo de hardware LoRa ESP32 (Heltec V3, T-Echo, o T-Beam v1.2)",
                "Batería recargable Li-Ion 18650 de alta retención (3200 mAh+)",
                "Antena dipolar flexible sintonizada a frecuencia nacional regulada (915 MHz en América)"
            ),
            tools = listOf(
                "Instalador offline en 05_SOFTWARE_E_INSTALADORES_OFFLINE",
                "Cable USB-C OTG blindado para enlace serial directo"
            ),
            description = "Infraestructura troncal ideal para telecomunicaciones tácticas. Protocolo libre descentralizado inalámbrico que opera independientemente de torres celulares.",
            fieldUtility = "Crea un clúster local de comunicación de emergencia. Enruta textos y balizas de coordenadas entre nodos distantes formando una malla resiliente.",
            setupSteps = listOf(
                "1. Conectar el nodo LoRa por USB OTG o Bluetooth LE a NOMAD Tactical Hub.",
                "2. Seleccionar la banda de radiofrecuencia local (915 MHz en Argentina/América).",
                "3. Configurar canal con llave simétrica AES-256 y firma Web of Trust.",
                "4. Ubicar nodos en puntos elevados respetando el despeje de la Zona de Fresnel."
            )
        ),
        RepoAnalysis(
            name = "NOMAD Ark USB (128GB+ exFAT + PAR2 + Faro Server)",
            level = "Esencial / Universal",
            difficultyColor = "Green",
            estimatedCost = "$12 - $25 USD (Pendrive Dual USB-C/A)",
            materials = listOf(
                "Pendrive Dual USB-C + USB-A de 128 GB o 256 GB formateado en exFAT",
                "Bolsa estanca con gel de sílice + triple capa de papel aluminio en lata metálica (Faraday)"
            ),
            tools = listOf(
                "Script constructor tools/nomad-ark-builder/build_nomad_ark.py",
                "Cualquier teléfono Android con OTG, lector Kindle/Kobo o PC/Laptop"
            ),
            description = "El Arca de Conocimiento y Supervivencia Universal de 8 niveles (carpetas 00 a 07) con conversión automática a PDF, EPUB para Kindle, HTML OLED/E-Ink, TXT, paridad auto-reparable PAR2 e IA local GGUF.",
            fieldUtility = "Garantiza acceso instantáneo en menos de 60 segundos a medicina de trauma, potabilización, radio Baofeng, huerta INTA, mapas offline, Wikipedia médica .zim y servidor Wi-Fi Faro para abastecer a 50+ vecinos.",
            setupSteps = listOf(
                "1. Formatear la unidad USB de 128GB+ en sistema de archivos exFAT.",
                "2. Ejecutar python tools/nomad-ark-builder/build_nomad_ark.py para compilar la estructura 00 a 07.",
                "3. Abrir START_NOMAD_ARK.html en cualquier navegador o ejecutar START_COMMUNITY_BEACON para compartir por Wi-Fi.",
                "4. Ejecutar VERIFY_AND_REPAIR_ARK periódicamente para validar firmas SHA-256 y reparar sectores con PAR2."
            )
        ),
        RepoAnalysis(
            name = "Kiwix & Internet-in-a-Box (IIAB)",
            level = "Intermedio / Comunitario",
            difficultyColor = "Orange",
            estimatedCost = "$10 - $60 USD",
            materials = listOf("Unidad NOMAD Ark USB o Raspberry Pi", "Archivos .zim (wikipedia_es_all_nopic.zim, mdwiki_en_all_maxi.zim)"),
            tools = listOf("Kiwix APK / Kiwix Portable incluidos en carpeta 05_SOFTWARE_E_INSTALADORES_OFFLINE"),
            description = "Lector y servidor de enciclopedias comprimidas .ZIM que permite alojar toda la Wikipedia en español y la Enciclopedia Médica Offline sin internet.",
            fieldUtility = "Convierte cualquier teléfono o laptop en la Biblioteca de Alejandría médica y técnica de un refugio comunitario.",
            setupSteps = listOf(
                "1. Instalar Kiwix.apk o abrir Kiwix-Portable desde la carpeta 05 del pendrive NOMAD Ark.",
                "2. Cargar los archivos .zim ubicados en 04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/01_WIKIPEDIA_ZIM/.",
                "3. Activar la opción 'Servidor Local' en Kiwix o usar START_COMMUNITY_BEACON para que todo el refugio consulte desde sus celulares."
            )
        )
    )

    val survivalGuidesList = listOf(
        GuideItem(
            title = "00. PROTOCOLO DE LOS PRIMEROS 60 SEGUNDOS (TRIAJE INMEDIATO)",
            iconName = "warning",
            category = "Inmediato 60s",
            arkFolderRoute = "00_INDICE_MAESTRO_Y_PROTOCOLOS/00_INDICE_MAESTRO_EMERGENCIAS",
            arkPageCitation = "Sección A, Pág. 1-2",
            content = """
                CUANDO CADA SEGUNDO DECIDE LA VIDA, NO BUSQUE EN LIBROS LARGOS. EJECUTE EN ORDEN MARCH:
                
                1. M — HEMORRAGIA MASIVA (Massive Bleeding):
                   - Presione directamente la herida con gasa o tela limpia usando todo su peso.
                   - Si la sangre brota a chorros (arterial) en brazo o pierna: coloque TORNIQUETE TÁCTICO de 5 a 7 cm arriba de la herida (NUNCA sobre codo o rodilla). Gire el molinete hasta que cese el pulso distal y el sangrado.
                   - Anote la HORA EXACTA en la frente del herido (T = HH:MM) y active el Temporizador de Torniquete en la pestaña CÁLCULOS.
                
                2. A — VÍA AÉREA Y ASFIXIA (Airway):
                   - Si está inconsciente: maniobra frente-mentón (o tracción mandibular si sospecha trauma cervical). Limpie sangre o vómito de la boca. Coloque en Posición Lateral de Seguridad.
                
                3. R — RESPIRACIÓN Y PARO CARDÍACO (Respiration & CPR):
                   - Si no respira normalmente: active el METRÓNOMO RCP 110 BPM en esta app y comprima fuerte y rápido en el centro del esternón (5-6 cm de profundidad, 100 a 120 compresiones por minuto).
                
                4. C — CIRCULACIÓN Y SHOCK (Circulation):
                   - Eleve piernas si no hay fractura, controle sangrados menores con vendaje compresivo.
                
                5. H — HIPOTERMIA Y QUEMADURAS (Hypothermia / Head):
                   - Un herido que se enfría no coagula y muere.Envuelva inmediatamente en manta Mylar aislándolo del suelo frío.
            """.trimIndent(),
            tips = listOf(
                "Palabra clave en Lupa NOMAD Ark: TORNIQUETE, BLEEDING, RCP, MARCH, SHOCK.",
                "Use el botón de VOZ MANOS LIBRES si tiene ambas manos ocupadas conteniendo una hemorragia."
            )
        ),
        GuideItem(
            title = "01. Primeros Auxilios, Trauma, Suero Oral OMS y Dosificación",
            iconName = "medical_services",
            category = "Medicina",
            arkFolderRoute = "01_INMEDIATO_PRIMERAS_HORAS/01_MEDICINA_TRAUMA_Y_TRIAJE",
            arkPageCitation = "Donde no hay doctor / OMS, Pág. 14-88",
            content = """
                SUERO DE REHIDRATACIÓN ORAL CASERO EXACTO (ESTÁNDAR OMS):
                - En 1 LITRO de agua previamente hervida o clorada:
                  * SAL FINA: 1/2 cucharadita rasa (2.6 gramos). ¡NUNCA exceder! El exceso de sal causa hipernatremia letal en niños.
                  * AZÚCAR COMÚN: 6 cucharaditas rasas (13.5 a 18 gramos).
                - Administrar a sorbos pequeños cada 5 minutos en diarrea aguda, cólera, vómitos o quemaduras extensas.
                
                QUEMADURAS GRAVES (REGLA DE WALLACE Y FÓRMULA DE PARKLAND):
                - Enfríe con agua limpia a temperatura ambiente por 15-20 min (NUNCA hielo directo).
                - Reposición hídrica en 24h = 4 mL × Peso (kg) × % Superficie Corporal Quemada (50% en las primeras 8h desde el accidente, 50% en las siguientes 16h).
                
                VADEMÉCUM DE EMERGENCIA (VERIFICAR ALERGIAS SIEMPRE):
                - Paracetamol (Dolor/Fiebre): Adultos 500-1000 mg c/6-8h (Máx 4000 mg/día). Niños: 15 mg/kg/dosis.
                - Ibuprofeno (Inflamación): Adultos 400 mg c/8h. Niños: 10 mg/kg/dosis. PROHIBIDO en sospecha de Dengue o sangrado digestivo.
                - Amoxicilina (Infección respiratoria/dental/herida): Adultos 500 mg c/8h por 7-10 días. Niños: 45 mg/kg/día dividido c/8h.
                - Doxiciclina (Profilaxis agua contaminada/Leptospirosis): Adultos 100 mg c/12h con abundante agua.
            """.trimIndent(),
            tips = listOf(
                "Nunca afloje un torniquete una vez colocado en campo sin soporte quirúrgico.",
                "Use la Calculadora Médica en la pestaña CÁLCULOS para obtener la dosis exacta por kilogramo de peso."
            )
        ),
        GuideItem(
            title = "02. Potabilización Exacta de Agua (Tablas por g/L de Lavandina)",
            iconName = "water_drop",
            category = "Agua",
            arkFolderRoute = "02_CORTO_PLAZO_DIAS_Y_SEMANAS/01_AGUA_POTABILIZACION_Y_ACOPIO",
            arkPageCitation = "Manual OMS Potabilización, Pág. 5-19",
            content = """
                ERROR CRÍTICO A EVITAR: No todas las lavandinas tienen la misma concentración. Verifique en el envase los gramos de cloro activo por litro (g Cl/L) y use lavandina común SIN lavanda ni detergentes.
                
                TABLA EXACTA PARA 1 LITRO DE AGUA CLARA (2 mg/L) — Duplicar si es agua turbia (4 mg/L):
                - Lavandina de 25 g/L (2.5%): 2 gotas por litro (4 gotas si es turbia). Para 20 L = 1.6 mL (32 gotas).
                - Lavandina de 40 g/L (4.0%): 1 a 2 gotas por litro. Para 20 L = 1.0 mL (20 gotas).
                - Lavandina de 55 g/L (5.5% común en Argentina): 1 gota por litro (2 gotas si es turbia). Para 20 L = 0.73 mL (15 gotas).
                - Lavandina de 80 g/L (Concentrada): 1 gota cada 2 litros. Para 20 L = 0.50 mL (10 gotas).
                * REPOSO OBLIGATORIO: Agitar y dejar reposar 30 minutos a la sombra (60 min si el agua está muy fría o turbia).
                
                EBULLICIÓN SEGÚN ALTITUD BAROMÉTRICA:
                - Nivel del mar a 1500 msnm: Hervor vigoroso continuo durante 1 minuto.
                - Más de 2000 msnm (Cordillera/Puna): Hervor continuo durante 3 minutos (el agua hierve a menor temperatura por menor presión atmosférica).
            """.trimIndent(),
            tips = listOf(
                "Siempre filtre primero el agua turbia con tela de algodón doblada o filtro de arena/carbón antes de clorar.",
                "El método solar SODIS requiere botellas PET transparentes al sol directo durante 6 horas (o 48h si está nublado)."
            )
        ),
        GuideItem(
            title = "03. Radio Baofeng UV-5R por Teclado, Frecuencias y Antenas",
            iconName = "wifi_tethering",
            category = "Comunicaciones",
            arkFolderRoute = "02_CORTO_PLAZO_DIAS_Y_SEMANAS/06_COMUNICACIONES_RADIO_Y_ANTENAS",
            arkPageCitation = "Manual Radio de Emergencia, Pág. 10-42",
            content = """
                PROGRAMACIÓN MANUAL DE BAOFENG UV-5R SIN PC (MODO VFO):
                1. Presione el botón naranja [VFO/MR] para entrar en modo Frecuencia (VFO).
                2. Presione [A/B] para elegir la línea superior (A).
                3. Digite en el teclado numérico los 6 dígitos de la frecuencia (ej: 1 4 6 5 2 0 para 146.520 MHz).
                4. Para ahorrar batería y lograr máximo alcance directo sin repetidora: pulse [MENU] -> digite [2][5] (SFT-D) -> [MENU] -> elija OFF -> [MENU] -> [EXIT].
                5. Para ajustar potencia Alta (5W) o Baja (1W): [MENU] -> [2] (TXP) -> [MENU] -> HIGH/LOW -> [MENU].
                6. Para transmitir paquetes acústicos AFSK o imágenes SSTV desde NOMAD Tactical Hub sin cable: active VOX en [MENU] -> [4] (VOX) -> nivel 2 o 3, y acerque el altavoz del teléfono al micrófono del Baofeng.
                
                FRECUENCIAS VITALES DE EMERGENCIA (SUDAMÉRICA E INTERNACIONAL):
                - VHF Llamada Internacional / Socorro Terrestre: 146.520 MHz (FM Simplex).
                - VHF Socorro Marítimo / Prefectura Naval: 156.800 MHz (Canal 16 Marino).
                - VHF Emergencia Aeronáutica Civil: 121.500 MHz (AM).
                - UHF PMR446 (Walkie-Talkies sin licencia): Canal 3 = 446.03125 MHz (Subtono CTCSS 14 / 107.2 Hz) y Canal 8 = 446.09375 MHz.
                - UHF FRS/GMRS (América): Canal 1 = 462.5625 MHz / Canal 3 = 462.6125 MHz / Canal 20 = 462.6750 MHz.
                - Banda Ciudadana CB (camioneros y rutas): Canal 9 Emergencia = 27.065 MHz (AM) / Canal 19 Rutero = 27.185 MHz.
                - LoRa Meshtastic (Región ANZ/América): 915.0 MHz (Canal LongFast por defecto).
                
                REGLA 3-3-3 DE ESCUCHA DE EMERGENCIA (AHORRO DE BATERÍA):
                - Encienda su radio cada 3 horas exactas (00:00, 03:00, 06:00, 09:00, 12:00, 15:00, 18:00, 21:00) durante los primeros 3 minutos para escuchar boletines sin agotar la batería.
            """.trimIndent(),
            tips = listOf(
                "Nunca presione el botón PTT de una radio sin tener la antena conectada; quemará el transistor final de salida.",
                "Use la pestaña COMMS para emitir y decodificar tramas NEPP AFSK, Morse 800Hz e imágenes SSTV."
            )
        ),
        GuideItem(
            title = "04. Defensa Nuclear, Radiológica (Regla 7-10), Química y EMP",
            iconName = "wb_sunny",
            category = "Nuclear / NRBQ",
            arkFolderRoute = "01_INMEDIATO_PRIMERAS_HORAS/02_DEFENSA_CIVIL_GUERRA_Y_NUCLEAR",
            arkPageCitation = "Nuclear War Survival Skills & Suecia MSB, Pág. 22-65",
            content = """
                REGLA DEL 7-10 DE DECAIMIENTO RADIACTIVO (GEIGER):
                - Por cada incremento de 7 veces en el tiempo tras la detonación, la tasa de radiación cae a la décima parte (1/10):
                  * 1 hora = 100% (ej: 1000 R/h letal).
                  * 7 horas = 10% (100 R/h).
                  * 49 horas (~2 días) = 1% (10 R/h).
                  * 343 horas (~14 días) = 0.1% (1 R/h).
                
                ESPESORES DE CAPA HEMIRREDUCTORA GAMMA (HVL — Reduce radiación al 50% por cada capa):
                - Plomo: 1.2 cm | Acero: 2.5 cm | Hormigón: 6.1 cm | Tierra compactada: 9.1 cm | Agua: 18.0 cm | Madera: 29.0 cm.
                - Con 60 cm de tierra compactada sobre el techo de una trinchera o sótano se bloquea más del 98.5% de los rayos Gamma.
                
                JAULA DE FARADAY CASERA PARA PROTEGER EL PENDRIVE NOMAD ARK Y RADIOS:
                - Envuelva el pendrive NOMAD Ark, un celular viejo con los APKs instalados y la radio Baofeng (con batería desconectada) en plástico aislante o cartón con sílice, y luego rodéelos con 3 capas independientes de papel de aluminio grueso bien plegado dentro de una lata metálica cerrada.
            """.trimIndent(),
            tips = listOf(
                "El Yoduro de Potasio (KI 130 mg adultos) solo protege la tiroides contra Yodo-131; el blindaje físico es insustituible.",
                "En caso de caída de polvo radiactivo, retire la ropa externa fuera del refugio (elimina el 90% de la contaminación) y lave piel con agua y jabón sin raspar."
            )
        ),
        GuideItem(
            title = "05. Huerta Calórica INTA, Autosuficiencia, Energía 12V y Jabón",
            iconName = "fireplace",
            category = "Autosuficiencia",
            arkFolderRoute = "03_LARGO_PLAZO_MESES_Y_ANIOS/01_AGRICULTURA_HUERTA_Y_SEMILLAS_INTA",
            arkPageCitation = "Manual ProHuerta INTA & Con nuestros propios esfuerzos, Pág. 8-120",
            content = """
                CULTIVOS DE MÁXIMA DENSIDAD CALÓRICA POR METRO CUADRADO (PRIORIDAD DE SIEMBRA):
                1. Papa y Batata (Camote): Mayor rendimiento calórico por m² y almacenamiento subterráneo natural.
                2. Maíz + Poroto + Zapallo (Asociación 'Las Tres Hermanas'): El maíz da soporte, el poroto fija nitrógeno en el suelo y el zapallo cubre la tierra reteniendo humedad y aportando calorías y semillas grasas.
                3. Amaranto, Quinoa, Lenteja y Girasol: Proteína completa y extracción de aceite.
                
                CONSERVACIÓN DE ALIMENTOS SIN ELECTRICIDAD:
                - Charqui / Salazón: Cortar carne magra en láminas finas (<5 mm), cubrir en sal gruesa (24-48h) y secar al sol/viento protegida con malla mosquitera.
                - Olla Bruja (Cocción Térmica sin Gasto de Leña): Hervir la olla con legumbres/arroz durante 10 minutos y meterla inmediatamente dentro de una caja o pozo aislado con lana, frazadas, aserrín o telgopor; se termina de cocinar sola en 3 horas ahorrando el 80% del combustible.
                
                QUÍMICA ESENCIAL DE SUPERVIVENCIA (JABÓN Y DESINFECTANTE):
                - Lejía de Ceniza (Potasa KOH): Llenar un balde perforado en su base (con filtro de paja y grava) con ceniza blanca de madera dura y verter agua hirviendo lentamente. El líquido jabonoso resultante está listo cuando un huevo fresco o una papa pequeña flota en él. Hervir a fuego lento con grasa animal derretida hasta saponificar.
            """.trimIndent(),
            tips = listOf(
                "Guarde siempre el 15% de las mejores plantas de polinización abierta (no híbridas F1) para producir semilla propia.",
                "Use la Calculadora Solar 12V/24V en la pestaña CÁLCULOS para dimensionar baterías de auto y cables sin riesgo de sobrecalentamiento."
            )
        )
    )
}
