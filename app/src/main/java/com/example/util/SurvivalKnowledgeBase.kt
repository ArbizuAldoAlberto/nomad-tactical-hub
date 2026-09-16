package com.example.util

data class GuideItem(
    val title: String,
    val iconName: String,
    val category: String,
    val content: String,
    val tips: List<String>
)

data class RepoAnalysis(
    val name: String,
    val level: String, // "Básico (PWA)", "Intermedio (Lector ZIM)", "Avanzado (Servidor Pi)", "Táctico (Físico + OS)"
    val difficultyColor: String, // "Green", "Orange", "Red", "Cyan"
    val estimatedCost: String,
    val materials: List<String>,
    val tools: List<String>,
    val description: String,
    val fieldUtility: String,
    val setupSteps: List<String>
)

object SurvivalKnowledgeBase {

    val prepperReposList = listOf(
        RepoAnalysis(
            name = "Project Nomad (Crosstalk Solutions)",
            level = "Avanzado / Táctico (Hardware + Software)",
            difficultyColor = "Red",
            estimatedCost = "$180 - $450 USD",
            materials = listOf(
                "Computador de placa reducida (Raspberry Pi 4 4GB or RockPi SBC)",
                "Nodo de radio LoRa Meshtastic (Heltec V3 o LILYGO T-Beam)",
                "Antena para exteriores de 5.8 dBi (sintonizada a 915/868 MHz)",
                "Gabinete hermético grado militar IP67 / Pelican Case",
                "Controlador solar de carga robusto de 5V y batería LiFePO4 de 12V 10Ah",
                "Módulo deGPS USB U-blox de bajo consumo para telemetría autónoma"
            ),
            tools = listOf(
                "Soldadora electrónica portátil de estaño",
                "Software BalenaEtcher de grabado flash de firmware",
                "Aplicación móvil de sincronización con cifrado AES-256"
            ),
            description = "Un ecosistema de comunicaciones y cómputo táctico móvil de mochila diseñado para ofrecer transmisiones de largo alcance y en malla descentralizada sin conexión terrestre.",
            fieldUtility = "Interconecta terminales móviles con mapas locales fuera de línea de la cuenca, permitiendo el despliegue de balizas de salvamento, geolocalización continua por GPS de pelotones y mensajería encriptada regional por radiofrecuencia.",
            setupSteps = listOf(
                "1. Flashear el firmware oficial de Meshtastic en el chip de LoRa de tu placa.",
                "2. Ensamblar e impermeabilizar la Raspberry Pi y el cargador solar en el maletín de campaña.",
                "3. Conectar el receptor de GPS interno y fijar el cable de extensión hacia la varilla de antena coaxial.",
                "4. Enlazar este dispositivo Android al punto de acceso local Wi-Fi / Bluetooth del chasis Nomad para iniciar cartografía y rastreo táctico."
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
                "Antena dipolar flexible sintonizada a frecuencia nacional regulada"
            ),
            tools = listOf(
                "Navegador Chrome para flash directo desde flasher.meshtastic.org",
                "Cable USB-C blindado para depuración de firmware"
            ),
            description = "La infraestructura troncal ideal para telecomunicaciones tácticas. Es un protocolo libre descentralizado inalámbrico que opera independientemente de torres celulares.",
            fieldUtility = "Crea un clúster local de comunicación de emergencia. Enruta de manera inteligente textos y balizas de coordenadas entre nodos distantes formando una malla resiliente impenetrable.",
            setupSteps = listOf(
                "1. Conectar tu nodo receptor LoRa por USB e ingresar al flasher de Meshtastic.",
                "2. Seleccionar firmware e indicar la banda de radiofrecuencia local permitida (ej: 915 MHz para América, 868 MHz para Europa).",
                "3. Configurar un canal privado con llaves cifradas simétricas de seguridad militar AES-256.",
                "4. Distribuir nodos en tejados y colinas elevadas para ampliar la cobertura táctica hasta 15+ kilómetros."
            )
        ),
        RepoAnalysis(
            name = "Survival_Kit (BEKO2210)",
            level = "Fácil",
            difficultyColor = "Green",
            estimatedCost = "$0 USD (Gratuito)",
            materials = listOf("Cualquier smartphone táctico o tablet", "Navegador web con soporte PWA"),
            tools = listOf("Conexión inicial para descarga en caché"),
            description = "Una Progressive Web App (PWA) enfocada de forma pura en preparación táctica, gestión ágil de inventarios de despensa, checklists de mochilas de emergencia (Bug Out Bag) y protocolos estándar de crisis.",
            fieldUtility = "Ideal para preppers de ciudad y familias. Sirve como lista de verificación ágil fuera de línea para controlar stock de comida y equipo de emergencia de forma interactiva.",
            setupSteps = listOf(
                "1. Acceder al repositorio github.com/BEKO2210/Survival_kit",
                "2. Ejecutar de forma local o abrir el link de despliegue en el navegador móvil.",
                "3. Seleccionar 'Añadir a pantalla de inicio' en las opciones del navegador móvil.",
                "4. La app se guardará en caché y se ejecutará de forma autónoma sin acceso a internet."
            )
        ),
        RepoAnalysis(
            name = "Kiwix (Offline Reader)",
            level = "Intermedio",
            difficultyColor = "Orange",
            estimatedCost = "$10 - $30 USD (Tarjeta SD)",
            materials = listOf("Tarjeta MicroSD de alta capacidad (64GB - 512GB)", "Un celular Android o PC de bajo consumo"),
            tools = listOf("Aplicación cliente Kiwix (Android/Desktop)", "Lector de tarjetas SD"),
            description = "El pilar central del almacenamiento de conocimiento local. Lector optimizado de archivos comprimidos .ZIM que permite empaquetar sitios web enteros sin pérdida de enlaces.",
            fieldUtility = "Permite llevar de forma local la Wikipedia entera en español (con o sin imágenes), WikiMed (enciclopedia médica de emergencia completa), manuales militares sobre ingeniería de campo y foros prepper enteros.",
            setupSteps = listOf(
                "1. Instalar la app Kiwix para Android desde repositorio confiable o APK directa.",
                "2. Visitar library.kiwix.org para ver catálogo de archivos compilados .zim.",
                "3. Descargar archivos masivos esenciales (ej: 'Wikipedia en español', 'WikiMed', 'Wikihow offline').",
                "4. Mover los archivos .zim a la tarjeta MicroSD y cargarlos localmente desde el lector Kiwix."
            )
        ),
        RepoAnalysis(
            name = "Internet-in-a-Box (IIAB)",
            level = "Avanzado",
            difficultyColor = "Red",
            estimatedCost = "$45 - $90 USD",
            materials = listOf("Raspberry Pi 3/4 o mini-PC antiguo", "Tarjeta MicroSD Extreme (64GB - 128GB)", "Fuente de alimentación portátil de 5V/3A"),
            tools = listOf("Software BalenaEtcher", "Script instalador Ansible de IIAB"),
            description = "Crea un portal cautivo WiFi portátil autónomo ('La Biblioteca de Alejandría' de bolsillo). Cualquier dispositivo móvil cercano puede conectarse por señal inalámbrica sin usar internet tradicional.",
            fieldUtility = "Establece un repetidor local de información en búnkeres o refugios rurales compartidos. Suministra mapas locales de OpenStreetMap, videos didácticos de Khan Academy y libros en PDF en tiempo récord de forma cooperativa.",
            setupSteps = listOf(
                "1. Descargar la imagen de sistema operativo recomendada (Ubuntu/Raspbian) para la Raspberry Pi.",
                "2. Seguir las guías en github.com/iiab/iiab para iniciar el configurador de red.",
                "3. Utilizar el instalador interactivo vía terminal para desplegar el portal interactivo y módulos Kiwix.",
                "4. Configurar el hardware para emitir una red WiFi abierta de auxilio llamada 'REDE_DE_EMERGENCIA_INFO'."
            )
        ),
        RepoAnalysis(
            name = "refugiOS (Ganso)",
            level = "Táctico / Avanzado",
            difficultyColor = "Cyan",
            estimatedCost = "$5 - $15 USD (Pendrive)",
            materials = listOf("Pendrive USB 3.0 / 3.1 ultra-resistente de 32GB+", "Cualquier laptop x86 antigua o moderna de campo"),
            tools = listOf("Ventoy o Rufus (Software de quemado)"),
            description = "Un sistema operativo portátil completo Live-USB construido bajo Linux para la resiliencia digital extrema, comunicaciones en red local segura inalámbrica, anonimato y lectura de archivos de contingencia.",
            fieldUtility = "Permite revivir cualquier laptop o PC ajena en situaciones de redes derribadas o hackeadas. Ofrece un entorno seguro sin malware que preserva tus manuales tácticos y notas encriptadas.",
            setupSteps = listOf(
                "1. Descargar la imagen ISO compilada de refugiOS desde el enlace del repositorio del creador.",
                "2. Preparar el pendrive usando Ventoy o Rufus para garantizar la persistencia de datos.",
                "3. Conectar el pendrive en la PC táctica de campaña.",
                "4. Reiniciar la máquina e ingresar a la BIOS para arrancar desde la interfaz USB."
            )
        ),
        RepoAnalysis(
            name = "Doomsday Cyberdeck (EzioDEVio)",
            level = "Muy Avanzado / Táctico",
            difficultyColor = "Magenta",
            estimatedCost = "$250 - $600 USD",
            materials = listOf(
                "Maletín táctico estanco pelícano (Pelican Case 1150/1200)",
                "Raspberry Pi 4/5 con disipador de aluminio pasivo",
                "Pantalla LCD IPS táctil rugerizada HDMI de 7 pulgadas",
                "Receptor de Radio SDR (Soft Defined Radio) RTL-SDR V3/V4",
                "Antena telescópica de banda ancha (HF/VHF/UHF)",
                "Batería externa PowerBank LiFePO4 de alta durabilidad (PD30W+)",
                "Teclado mecánico ultra-compacto integrado al chasis"
            ),
            tools = listOf("Impresora 3D para fabricación de soportes", "Soldador de estaño de precisión", "Herramientas de bricolaje táctico"),
            description = "Documentación física y de software para ensamblar un 'Cyberdeck' rugerizado, a prueba de golpes, agua y polvo. Incluye monitoreo de radioaficionados en tiempo real, inteligencia artificial offline local (Ollama) y navegación GPS autónoma.",
            fieldUtility = "La estación de campo de supervivencia definitiva. Escucha transmisiones VHF/UHF de emergencia de servicios médicos, rastrea aviones cercanos, almacena gigabytes de guías de reanimación médica y opera un ayudante IA con modelos ligeros sin internet.",
            setupSteps = listOf(
                "1. Diseñar e imprimir las placas o soportes 3D para anclar la pantalla de 7\" y la Raspberry Pi al chasis del maletín.",
                "2. Instalar el receptor RTL-SDR en un puerto USB blindado para mitigar ruido térmico.",
                "3. Instalar un sistema Linux optimizado y desplegar el servicio de IA local Ollama con modelos 'TinyLlama' de 1.1 mil millones de parámetros.",
                "4. Integrar utilidades de cartografía terrestre como Marble u OpenCPN con un receptor USB GPS activo."
            )
        )
    )

    val survivalGuidesList = listOf(
        GuideItem(
            title = "Purificación de Agua de Emergencia",
            iconName = "water_drop",
            category = "Agua",
            content = """
                El agua es la prioridad número uno en cualquier escenario de supervivencia. Consumir agua sin tratar puede causar deshidratación severa por infecciones bacterianas (cólera, giardia).
                
                MÉTODOS EFECTIVOS:
                1. EBULLICIÓN: El método más seguro. Lleva el agua a un punto de ebullición vigorosa durante un mínimo de 1 minuto continuo (3 minutos a altitudes de montaña superiores a 2000m). Mata bacterias, virus y protozoos.
                
                2. CLORO / YODO: Utiliza cloro común sin aroma. Agrega 4 gotas de cloro por cada litro de agua clara (8 gotas si está turbia). Agita y deja reposar estrictamente por 30 minutos antes de consumir.
                
                3. FILTRACIÓN DE MEMBRANA: Filtros de tamaño de poro de 0.1 a 0.2 micras (como Lifestraw o filtros Sawyer) eliminan al 99.9% bacterias y parásitos, pero no detienen virus a menos que sea un purificador de grado militar.
                
                4. FILTRACIÓN SOLAR (SODIS): Llena botellas plásticas PET transparentes y colócalas tumbadas bajo el sol directo continuo sobre metal caliente durante un mínimo de 6 horas estables (o 2 días si el cielo está nublado).
            """.trimIndent(),
            tips = listOf(
                "Evita colectar agua estancada si dispone de escorrentía activa.",
                "Siempre decanta o filtra previamente los lodos usando tela o algodón de playera.",
                "Conserva el agua filtrada limpia en pocillos sellados para evitar re-contaminación."
            )
        ),
        GuideItem(
            title = "Construcción de Refugios y Fuego de Campaña",
            iconName = "fireplace",
            category = "Shelter",
            content = """
                La hipotermia es un asesino silencioso y letal rápido en climas hostiles fríos o lluviosos extremos.
                
                REFUGIO DE HOJARASCA (Debris Hut):
                1. Busca un tronco caído o una rama gruesa y apóyala en un tocón o bifurcación de árbol de forma estable en un ángulo de unos 30-45 grados.
                2. Coloca ramas secundarias a ambos lados de la viga central simulando una jaula en 'A' cerrada.
                3. Cubre toda la estructura exterior con una capa masiva de hojas secas, ramas, musgo y barro de al menos 60 cm de espesor continuo.
                4. Rellena el interior habitable con hojas suaves y secas de la misma altura para crear un colchón de aire aislante del suelo helado.
                
                CONSTRUCCIÓN DE FUEGO SEGURO:
                - Asegura tres tipos de materiales combustibles:
                  a) Yesca (paja fina, nido de aves secos, resina de pino, corteza de abedul de raspado).
                  b) Astillas medianas de ignición del grosor de un lápiz.
                  c) Troncos de madera seca densa para combustión prolongada.
                - Organiza un contrafuerte plano de piedras o tierra removida para resguardar la base del viento dominante.
            """.trimIndent(),
            tips = listOf(
                "Never duermas directamente en el suelo húmedo; aislarte de la tierra es tan vital como el refugio mismo.",
                "Coloca reflectores de piedras detrás de la fogata para redirigir el calor radiante de frente a tu refugio táctico."
            )
        ),
        GuideItem(
            title = "Primeros Auxilios de Campaña",
            iconName = "medical_services",
            category = "Medicina",
            content = """
                En una situación de aislamiento hostil, controlar hemorragias externas extremas masivas y estabilizar traumatismos es la diferencia entre la vida y la muerte.
                
                CONTROL DE HEMORRAGIAS (Protocolo MARCH):
                1. PRESIÓN DIRECTA: Presiona firmemente el punto de sangrado con una gasa esterilizada o venda limpia usando el peso de tu propio cuerpo.
                2. TORNIQUETE TÁCTICO: Para sangrado masivo incontrolable en extremidades. Colócalo de 5 a 10 cm arriba de la herida abierta (nunca sobre una articulación como rodillas o codos). Ajusta el molinete firmemente hasta detener el sangrado por completo. Anota estrictamente la hora de colocación en el chasis del torniquete.
                3. EMPAQUETADO DE HERIDAS: Rellena cavidades sangrantes (ingle, axilas) usando vendaje de gasa hemostática y aplica presión directa pesada ininterrumpida por al menos 3 minutos estables de reloj.
                
                TRATAMIENTO DE FRACTURAS:
                - Inmoviliza el hueso fracturado utilizando férulas improvisadas con ramas robustas, cartón o mantas enrolladas.
                - Asegura que la férula abarque la articulación superior e inferior a la lesión para evitar desplazamientos catastróficos.
            """.trimIndent(),
            tips = listOf(
                "Un torniquete colocado correctamente genera gran dolor; nunca lo aflojes una vez asegurado o liberarás coágulos letales.",
                "Mantén caliente por encima de todo al paciente herido; la hipotermia extrema interrumpe los factores naturales de coagulación sanguínea."
            )
        ),
        GuideItem(
            title = "Comunicaciones y Señalización de Auxilio",
            iconName = "wifi_tethering",
            category = "Comunicaciones",
            content = """
                Ser detectado y rescatado depende de tu capacidad de transmitir señales legibles nacionales e internacionales de emergencia.
                
                SEÑALES VISUALES TÁCTICAS:
                1. REGLA DE TRES: Tres fuegos colocados en una perfecta línea exacta de separación (o triángulo amplio de 25m) representa la señal internacional inequívoca de auxilio.
                2. ESPEJO DE SEÑALES (Heliógrafo): Apunta el flash de reflexión solar a aeronaves o barcos lejanos. Coloca un dedo en el horizonte frente a la aeronave de mira, asoma el espejo cerca de tu ojo para dirigir el haz centrado de luz intermitente.
                
                COMUNICACIONES DE RADIO (SDR / VHF):
                - Frecuencia internacional satelital de socorro civil de aviación: 121.500 MHz (Banda militar de rescate: 243.000 MHz).
                - En banda terrestre VHF de emergencia de aficionados (GMRS/PMR): Sintoniza Canal 3 PMR446 (446.031 MHz) o Canal 9 de banda ciudadana CB en 27.065 MHz (Modulación AM).
                - Transmite mensajes de voz mediante el protocolo estándar de emergencia: 'MAYDAY, MAYDAY, MAYDAY' seguido de tus coordenadas exactas actuales, naturaleza de la crisis e inventario de heridos.
            """.trimIndent(),
            tips = listOf(
                "Transmite mensajes cortos para conservar la valiosa energía de las baterías de tu radio.",
                "Si la señal analógica es muy ruidosa, pulsa el botón PTT en intervalos cortos formando código morse SOS estándar (... --- ...)."
            )
        ),
        GuideItem(
            title = "Tormentas Solares y Blindaje EMP (CME)",
            iconName = "wb_sunny",
            category = "Electromagnetismo",
            content = """
                Una gran Eyección de Masa Coronal (CME) o un ataque de Pulso Electromagnético (EMP) puede freír la red eléctrica de alta tensión, subestaciones y procesadores de silicio desprotegidos.
                
                PROTOCOLOS ANTE ALERTA NOAA G5:
                1. CONSTRUCCIÓN DE JAULAS DE FARADAY: Utiliza contenedores metálicos (como botes de munición, latas de aluminio para galletas o botes de pintura metálica limpia). Envuelve tus equipos críticos (transistores, radio VHF, cargador solar, linternas) individualmente en capas gruesas de plástico aislante (o bolsas ziploc) antes de ponerlos dentro de la lata. Ningún plástico protector debe tener roturas; el metal no debe tocar los circuitos electrónicos directement.
                
                2. SISTEMAS FOTOVOLTAICOS OFF-GRID: Desconecta físicamente tus paneles solares de los controladores de carga durante la cresta geomagnética. El cableado expuesto largo actúa como antena inductora que colecta voltajes masivos fatales.
                
                3. VEHÍCULOS ANTIGUOS: Protege o prioriza los motores de combustión mecánica carburada puramente analógica (anteriores a 1980, sin ECUs electrónicas). Serán los únicos transportes móviles funcionales en el colapso absoluto de chips informáticos.
            """.trimIndent(),
            tips = listOf(
                "Utiliza papel de aluminio multicapa (mínimo 3 capas continuas) frotando perfectamente las juntas si no dispones de botes metálicos rígidos.",
                "Los cables de antenas exteriores y líneas telefónicas de cobre representan el mayor vector de introducción de corriente transitoria."
            )
        ),
        GuideItem(
            title = "Inundaciones Extremas e Hidrodinámica",
            iconName = "beach_access",
            category = "Inundaciones",
            content = """
                Las inundaciones masivas por colapso de presas, huracanes climáticos extremos o lluvias excepcionales causan el 90% de las fatalidades naturales inmediatas de campo.
                
                TÁCTICAS DEFENSIVAS DE EVACUACIÓN:
                1. ESCAPE HACIA TIERRAS ALTAS: No esperes la orden de retirada. Un aumento veloz de 15 cm de agua arrastra un calzado adulto resistente. 50 cm de agua movilizan y voltean camiones pesados y vehículos 4x4.
                
                2. SOGA DE ARRASTRE SEGURO: Si debes cruzar una corriente activa inevitable, ancla la soga en un punto alto del tronco río arriba para deslizarte transversalmente en ángulo oblicuo de 45 grados (método del péndulo).
                
                3. BALSAS DE EMERGENCIA TÁCTICAS: Puedes crear una balsa improvisada envolviendo una lona impermeable lona pesada alrededor de un lecho denso de ramas verdes, follaje de bajo peso molecular aislante y botellas plásticas vacías unidas por cuerda paracord.
                
                4. DESINFECCIÓN HIDROLÓGICA INTENSA: El agua de inundación contiene lodos cloacales y químicos industriales letales. Nunca la bebas directa incluso hervida sin antes decantarla, pasarla por filtros de arcilla/carbón activado denso y desinfectarla con doble cloro.
            """.trimIndent(),
            tips = listOf(
                "En inundaciones urbanas, evita sótanos, túneles de metro e intersecciones llanas; la acumulación de dióxido de carbono y descarga de transformadores rotos es letal.",
                "Ten siempre un silbato acoplado a tu camisa táctica para señalizar tu posición en el agua."
            )
        ),
        GuideItem(
            title = "Sequías y Extracción Extrema de Humedad",
            iconName = "wb_cloudy",
            category = "Sequías",
            content = """
                Bajo condiciones de sequías agudas de larga duración o desiertos extremos, el cuerpo humano colapsa rápidamente dentro de las 72 horas sin hidratamiento activo.
                
                TÉCNICAS DE COLECTA DE HUMEDAD DE SUPERVIVENCIA:
                1. DESTILADOR SOLAR DE SUBSUELO (Solar Still): Extrae agua del suelo mediante evaporación. Cava un pozo de 1 metro de diámetro por 60 cm de profundidad. Coloca un vaso o pocillo cilíndrico en el centro geométrico. Cubre el foso con una lámina de lona de polietileno plástico transparente. Asegura los bordes del plástico perfectamente con tierra húmeda. Coloca una pequeña piedra en el centro exacto del film plástico, justo arriba del vaso de colecta, para forzar un cono de caída. El calor solar evaporará la humedad freática, condensándola en el plástico, dirigiendo las gotas hacia tu vaso receptor.
                
                2. COLECTORES DE CONDENSACIÓN Y ROCÍO EXTRAÑO: Envuelve bolsas plásticas cerradas alrededor de ramas frondosas de árboles vivos no venenosos. La transpiración de las hojas liberará vapor de agua potable pura dentro de la bolsa durante el día.
                
                3. CONSERVACIÓN DE HUMEDAD: Quédate a la sombra en el subsuelo durante el pico del mediodía. Tapa tu boca y cabeza con un paño húmedo para mitigar pérdidas mucosas por respiración activa rápida.
            """.trimIndent(),
            tips = listOf(
                "Nunca bebas orina deshidratada de color ámbar oscuro; es altamente concentrada en sodio y sales nitrogenadas que dañan tus riñones.",
                "Ecarva en el lecho seco de un río de curva exterior; a menudo encontrarás humedad filtrada a menos de un metro bajo la grava."
            )
        ),
        GuideItem(
            title = "Física de Guerra Nuclear y Fallout Radiactivo",
            iconName = "warning",
            category = "Nuclear",
            content = """
                La amenaza de una detonación nuclear exige una comprensión matemática profunda de la radiación ionizante (Gama, Beta, Alfa) para planificar la preservación celular.
                
                LA REGLA DEL 7-10 DE DESINTEGRACIÓN (FÍSICA DE FALLOUT):
                - La intensidad de la radiación residual de los productos de fisión disminuye en un factor de 10 por cada aumento de 7 veces en el tiempo transcurrido desde la explosión.
                - Ejemplo: Si el fallout mide una tasa letal de 1000 Roentgens/hora (R/h) a la Hora 1:
                  * A las 7 horas, la tasa decae a 100 R/h (90% menos).
                  * A las 49 horas (~2 días), decae a 10 R/h (99% menos).
                  * A las 343 horas (~2 semanas), retrocede a 1 R/h (Zonas transitables bajo control de dosis).
                
                LOS TRES PILARES INMUTABLES DE DEFENSA RAD:
                1. BLINDAJE (Shielding): El poder de protección depende de la masa. La distancia media de reducción a la mitad (Half-Value Layer, HVL) para frenar radiación Gamma de alta energía es de:
                   * Plomo: 1.5 cm.
                   * Acero grueso: 2.5 cm.
                   * Hormigón denso: 6.0 cm.
                   * Tierra compactada o arena arcillosa: 10.0 cm.
                   * Agua limpia quieta: 18.0 cm.
                2. DISTANCIA (Distance): Ley de la inversa del cuadrado. Duplicar la distancia al foco radiactivo disminuye la dosis de intensidad a un cuarto (1/4).
                3. TIEMPO (Time): Minimiza el tiempo de exposición exterior acumulado. Mantente adentro de tu bunker improvisado al menos 14 días ininterrumpidos para proteger la médula ósea.
                
                POTASIO IODURO (KI):
                - Satura la glándula tiroides para evitar la absorción celular del nefasto Isótopo radiactivo Iodo-131 presente en el fallout de fisión reciente. Dosificación civil recomendada de 130mg diarios para adultos estables ante confirmación oficial.
            """.trimIndent(),
            tips = listOf(
                "Improvisa un detector casero o mantén sellado herméticamente tu dosímetro electrónico dentro de una caja blindadora metálica para evitar que sea dañado por el pulso electromagnético.",
                "Duchas continuas de descontaminación de lodo deben hacerse sin frotar con fuerza la dermis; evita acondicionadores de cabello que adsorben isótopos radiactivos en las fibras."
            )
        ),
        GuideItem(
            title = "Ataques Biológicos y Pandemias",
            iconName = "favorite",
            category = "Biológico",
            content = """
                Los patógenos armados (Ántrax, Peste, Viruela, ébola o toxina botulínica recombinante) son letales, inoloros, silenciosos e invisibles en escala micrométrica.
                
                SISTEMA CIVIL DE CONFINAMIENTO DE CRISIS:
                1. CUARENTENA Y SELLADO DE REFUGIOS: Configura una esclusa de aire doble en la puerta del refugio usando plástico de polietileno resistente (de 6 milésimas de espesor) y cinta americana industrial. Sella todas las ventanas, rejillas y grietas de aire exteriores usando la misma membrana estanca.
                
                2. EQUIPAMIENTO DE PROTECCIÓN PERSONAL (EPP):
                   - Máscara antigás militar de rostro completo con cartucho CBRN aprobado (filtro HEPA clasificación P100 absorbe esporas y patógenos en aerosol al 99.97%).
                   - Trajes impermeables de Tyvek sellables con cinta en puños y tobillos. Guantes de nitrilo triple capa.
                
                3. DESCONTAMINACIÓN CON CLORO COMPLETO:
                   - Para desinfectar herramientas expuestas y calzado: Usa una mezcla fresca de 1 parte de cloro concentrado por cada 9 partes de agua limpia (concentración del 0.5% activa). El hipoclorito de sodio degrada la membrana lipídica de los virus más rudos y el core proteico de las esporas de Bacillus anthracis en 10 minutos estables de remojo completo.
            """.trimIndent(),
            tips = listOf(
                "La esclusa de aire en la puerta del refugio debe tener un área de desvestido donde dejes la ropa contaminada antes de ingresar a la zona estéril interna.",
                "Asegura filtros HEPA para entrada de aire con ventilación forzada manual a fin de evitar presiones negativas que succionen microbios por las grietas."
            )
        )
    )
}
