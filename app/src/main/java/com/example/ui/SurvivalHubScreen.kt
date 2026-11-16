package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import com.example.util.GuideItem
import com.example.util.RepoAnalysis
import com.example.util.SurvivalKnowledgeBase
import com.example.util.CrisisCalculators
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurvivalHubScreen(
    viewModel: SurvivalViewModel,
    modifier: Modifier = Modifier
) {
    val currentTabState by viewModel.currentTab.collectAsStateWithLifecycle()
    val morseLightOnState by viewModel.morseLightOn.collectAsStateWithLifecycle()
    val cipherTextOutputState by viewModel.cipherTextOutput.collectAsStateWithLifecycle()

    var showSosToast by remember { mutableStateOf(false) }
    var sosToastMessage by remember { mutableStateOf("") }

    LaunchedEffect(cipherTextOutputState) {
        if (cipherTextOutputState.isNotBlank()) {
            sosToastMessage = "COMUNICACIÓN PROCESADA CON ÉXITO: " + cipherTextOutputState.take(15) + "..."
            showSosToast = true
            delay(4000)
            showSosToast = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta",
                            tint = CyberGreen,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "NOMAD TACTICAL HUB",
                                style = MaterialTheme.typography.titleLarge,
                                color = CyberGreen
                            )
                            Text(
                                text = "SISTEMA SEGURO FUERA DE LÍNEA // V4.1",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrayText
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateCharcoal,
                    titleContentColor = CyberGreen
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                val tabs = listOf(
                    Triple("panel", "PANEL", Icons.Default.Home),
                    Triple("guias", "GUÍAS", Icons.Default.Info),
                    Triple("unidad", "UNIDAD", Icons.Default.Favorite),
                    Triple("crisis", "CÁLCULOS", Icons.Default.Settings),
                    Triple("mapa", "GPS", Icons.Default.LocationOn),
                    Triple("cifrado", "COMMS", Icons.Default.Lock),
                    Triple("inventario", "STOCK", Icons.Default.List)
                )
                tabs.forEach { (tabId, label, icon) ->
                    val isSelected = currentTabState == tabId
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.currentTab.value = tabId },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) SlateCharcoal else LightIcon
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 9.sp
                                ),
                                color = if (isSelected) CyberGreen else GrayText
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyberGreen
                        ),
                        modifier = Modifier.testTag("nav_tab_$tabId")
                    )
                }
            }
        },
        containerColor = SlateCharcoal
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main content block
            when (currentTabState) {
                "panel" -> DashboardScreen(viewModel)
                "guias" -> GuidesScreen(viewModel)
                "unidad" -> SolidarityScreen(viewModel)
                "crisis" -> CrisisCalculatorScreen(viewModel)
                "mapa" -> MapScreen(viewModel)
                "cifrado" -> TransmissionsScreen(viewModel)
                "inventario" -> InventoryScreen(viewModel)
            }

            // Tactical Toast Banner Overlay
            AnimatedVisibility(
                visible = showSosToast,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, CyberGreen),
                    modifier = Modifier.fillMaxWidth().clickable { showSosToast = false }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "OK",
                            tint = CyberGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "COMUNICACIÓN CIFRADA",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                sosToastMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Morse full-screen flash light overlay
            AnimatedVisibility(
                visible = morseLightOnState,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WarnAmber)
                        .clickable { viewModel.stopMorseFlashing() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Destello SOS",
                            tint = SlateCharcoal,
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "EMITIENDO BALIZA SOS",
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = SlateCharcoal,
                                fontWeight = FontWeight.Black
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Pulsa en cualquier parte de la pantalla para desactivar",
                            style = MaterialTheme.typography.bodyLarge.copy(color = SlateCharcoal),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ------------------- SUB-SCREENS -------------------

@Composable
fun DashboardScreen(viewModel: SurvivalViewModel) {
    val compassDegree by viewModel.compassDegree.collectAsStateWithLifecycle()
    val isFlashingCurrently by viewModel.isFlashingCurrently.collectAsStateWithLifecycle()
    val rawTransmissions by viewModel.transmissions.collectAsStateWithLifecycle()
    val rawInventory by viewModel.inventoryItems.collectAsStateWithLifecycle()
    val barometerHpa by viewModel.barometerHpa.collectAsStateWithLifecycle()
    val magneticUt by viewModel.magneticFieldUt.collectAsStateWithLifecycle()
    val batteryPct by viewModel.batteryLevelPercent.collectAsStateWithLifecycle()
    val emconActive by viewModel.emconSilentMode.collectAsStateWithLifecycle()
    val cprRunning by viewModel.isCprMetronomeRunning.collectAsStateWithLifecycle()
    val cprBeat by viewModel.cprBeatCounter.collectAsStateWithLifecycle()
    val activeRingLabel by viewModel.activeNairRingLabel.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // System status cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ENLACE ALIMENTACIÓN / TELEMETRÍA 360° OMEGA",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("BARÓMETRO / EMF", style = MaterialTheme.typography.labelSmall, color = GrayText)
                                TacticalTooltip("Presión atmosférica real hPa y campo magnético en microteslas.")
                            }
                            Text(
                                "${String.format(Locale.US, "%.1f", barometerHpa)} hPa // ${String.format(Locale.US, "%.0f", magneticUt)} µT",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("BATERÍA LOCAL", style = MaterialTheme.typography.labelSmall, color = GrayText)
                                TacticalTooltip("Nivel energético monitoreado para enrutamiento automático NAIR.")
                            }
                            Text("$batteryPct% // ${if (emconActive) "EMCON SILENCIO" else "ACTIVO"}", style = MaterialTheme.typography.bodyMedium, color = CyberGreen)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("ESTADO DE ACCESO", style = MaterialTheme.typography.labelSmall, color = GrayText)
                                TacticalTooltip("Cifrado autenticado AES-256-GCM + Vigenère + OTP.")
                            }
                            Text("AES-256-GCM", style = MaterialTheme.typography.bodyMedium, color = BrightCyan)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleCprMetronome110Bpm() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (cprRunning) AlertRed else SurfaceMedium,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text(
                                if (cprRunning) "RCP 110 BPM (#$cprBeat) STOP" else "METRÓNOMO RCP 110 BPM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { viewModel.toggleEmconSilence(!emconActive) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (emconActive) WarnAmber else SurfaceMedium,
                                contentColor = if (emconActive) SlateCharcoal else CyberGreen
                            ),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text(
                                if (emconActive) "EMCON: SILENCIO RF ON" else "MODO SIGILO EMCON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "MOTOR IA ACTIVO: $activeRingLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrightCyan
                    )
                }
            }
        }

        // Radar Compass & Distress System
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SISTEMA DE ORIENTACIÓN MULTIBANDA",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberGreen,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tactical Radar Drawing
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .drawBehind {
                                // Draw Scope Background Grid
                                drawCircle(
                                    color = CyberGreen.copy(alpha = 0.1f),
                                    style = Stroke(2.dp.toPx())
                                )
                                drawCircle(
                                    color = CyberGreen.copy(alpha = 0.05f),
                                    radius = size.width / 3f,
                                    style = Stroke(1.dp.toPx())
                                )
                                drawCircle(
                                    color = CyberGreen.copy(alpha = 0.05f),
                                    radius = size.width / 6f,
                                    style = Stroke(1.dp.toPx())
                                )

                                // Crosshairs
                                drawLine(
                                    color = CyberGreen.copy(alpha = 0.15f),
                                    start = Offset(0f, size.height / 2f),
                                    end = Offset(size.width, size.height / 2f)
                                )
                                drawLine(
                                    color = CyberGreen.copy(alpha = 0.15f),
                                    start = Offset(size.width / 2f, 0f),
                                    end = Offset(size.width / 2f, size.height)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(160.dp)) {
                            // Rotate needle with degree State
                            rotate(degrees = compassDegree) {
                                // Draw compass pointer triangle
                                drawLine(
                                    color = CyberGreen,
                                    start = Offset(size.width / 2f, size.height / 2f),
                                    end = Offset(size.width / 2f, 15f),
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawCircle(
                                    color = CyberGreen,
                                    radius = 6.dp.toPx(),
                                    center = Offset(size.width / 2f, 15f)
                                )
                            }
                            // Glow center dot
                            drawCircle(
                                color = BrightCyan,
                                radius = 5.dp.toPx()
                            )
                        }
                        Text(
                            text = "${compassDegree.toInt()}° N",
                            color = CyberGreen,
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(SlateCharcoal.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CANAL DE SOCORRO RÁPIDO",
                        style = MaterialTheme.typography.labelSmall,
                        color = GrayText
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.startMorseFlashing("... --- ...") },
                            enabled = !isFlashingCurrently,
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = Color.White),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("sos_button")
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "SOS")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ENVIAR SOS", fontWeight = FontWeight.Bold)
                        }

                        if (isFlashingCurrently) {
                            Button(
                                onClick = { viewModel.stopMorseFlashing() },
                                colors = ButtonDefaults.buttonColors(containerColor = WarnAmber, contentColor = SlateCharcoal),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("PARAR SOS", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Advisor Assistant Block
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CONSEJERO TÁCTICO SATELITAL AI",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberGreen
                    )
                    Text(
                        text = "Enlaza con la inteligencia de apoyo en crisis",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val promptText by viewModel.advisorPrompt.collectAsStateWithLifecycle()
                    val resultText by viewModel.advisorResponse.collectAsStateWithLifecycle()
                    val isLoading by viewModel.isAdvisorLoading.collectAsStateWithLifecycle()

                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { viewModel.advisorPrompt.value = it },
                        label = { Text("Consultar protocolo táctico (ej: Purificar lodo)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("advisor_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGreen,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = CyberGreen,
                            unfocusedLabelColor = GrayText,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.askAdvisor() },
                            enabled = !isLoading && promptText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SlateCharcoal),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("ask_advisor_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SlateCharcoal)
                            } else {
                                Icon(Icons.Default.Share, contentDescription = "Consultar", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("EMITIR CONSULTA", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.quickAsk("¿Cómo detener hemorragias con torniquete?") },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceMedium),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text("Torniquete SOS", fontSize = 11.sp, color = CyberGreen)
                        }
                    }

                    if (resultText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateCharcoal)
                                .border(1.dp, BrightCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = "Respuesta", tint = BrightCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CONSEJO DE SUPERVIVENCIA:", style = MaterialTheme.typography.labelSmall, color = BrightCyan)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = resultText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Summary of data counters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("WAYPOINTS", style = MaterialTheme.typography.labelSmall, color = GrayText)
                        Text("${viewModel.waypoints.value.size} PUNTOS", style = MaterialTheme.typography.titleLarge, color = CyberGreen)
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("EQUIPO ACCESIBLE", style = MaterialTheme.typography.labelSmall, color = GrayText)
                        val total = rawInventory.size
                        val packed = rawInventory.filter { it.isPacked }.size
                        Text("$packed / $total EMP", style = MaterialTheme.typography.titleLarge, color = BrightCyan)
                    }
                }
            }
        }
    }
}

@Composable
fun GuidesScreen(viewModel: SurvivalViewModel) {
    var selectedCategory by remember { mutableStateOf("Todos") }
    val categories = listOf(
        "Todos", "Agua", "Shelter", "Medicina", "Comunicaciones", 
        "Electromagnetismo", "Inundaciones", "Sequías", "Nuclear", "Biológico", "Proyectos"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "ENCICLOPEDIA OFFLINE NOMAD",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberGreen
        )
        Text(
            text = "Banco de conocimientos y manuales de campo de supervivencia",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayText
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Scrollable Category Row Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) CyberGreen else SurfaceDark,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .border(1.dp, if (isSelected) CyberGreen else DarkBorder, RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = cat.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) SlateCharcoal else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle display of offline prepper repos vs manual guides
        if (selectedCategory == "Proyectos") {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepOlive.copy(0.4f))
                            .border(1.dp, CyberGreen, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Build, "Aegis Project", tint = CyberGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "PROYECTOS TÁCTICOS DIGITALES",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = CyberGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Instrucciones técnicas de infraestructura desconectada autónoma recabados de la comunidad prepper.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                items(SurvivalKnowledgeBase.prepperReposList) { repo ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = repo.name,
                                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                        color = Color.White
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Nivel: ${repo.level}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when (repo.difficultyColor) {
                                                "Green" -> CyberGreen
                                                "Orange" -> WarnAmber
                                                "Red" -> AlertRed
                                                "Cyan" -> BrightCyan
                                                else -> Color.White
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = repo.estimatedCost,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GrayText
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir",
                                    tint = GrayText
                                )
                            }

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = DarkBorder, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "DESCRIPCIÓN:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberGreen
                                )
                                Text(
                                    text = repo.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                                )

                                Text(
                                    text = "UTILIDAD EN EL CAMPO TÁCTICO:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrightCyan
                                )
                                Text(
                                    text = repo.fieldUtility,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                                )

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("MATERIALES:", style = MaterialTheme.typography.labelSmall, color = WarnAmber)
                                        repo.materials.forEach { mat ->
                                            Text("• $mat", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("HERRAMIENTAS:", style = MaterialTheme.typography.labelSmall, color = WarnAmber)
                                        repo.tools.forEach { tool ->
                                            Text("• $tool", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "PASOS DE INSTALACIÓN Y MONTAJE:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                repo.setupSteps.forEach { step ->
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 19.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                InteractiveRepoTool(repoName = repo.name)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        } else {
            // Filter standard offline survival guides
            val filteredGuides = if (selectedCategory == "Todos") {
                SurvivalKnowledgeBase.survivalGuidesList
            } else {
                SurvivalKnowledgeBase.survivalGuidesList.filter {
                    it.category.equals(selectedCategory, ignoreCase = true)
                }
            }

            if (filteredGuides.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay guías disponibles para esta categoría actualmente.", color = GrayText)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredGuides) { guide ->
                        var isExpanded by remember { mutableStateOf(false) }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (guide.category) {
                                                "Agua" -> Icons.Default.Home
                                                "Shelter" -> Icons.Default.Home
                                                "Medicina" -> Icons.Default.Favorite
                                                "Electromagnetismo" -> Icons.Default.Warning
                                                "Inundaciones" -> Icons.Default.PlayArrow
                                                "Sequías" -> Icons.Default.Search
                                                "Nuclear" -> Icons.Default.Warning
                                                "Biológico" -> Icons.Default.Favorite
                                                else -> Icons.Default.Share
                                            },
                                            contentDescription = guide.category,
                                            tint = CyberGreen,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = guide.title,
                                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "CATEGORÍA: ${guide.category.uppercase()}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BrightCyan
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expandir",
                                        tint = GrayText
                                    )
                                }

                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkBorder, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = guide.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        lineHeight = 21.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(DeepOlive.copy(alpha = 0.2f))
                                            .border(1.dp, CyberGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                "CONSEJOS RÁPIDOS DE CAMPO:",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CyberGreen
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            guide.tips.forEach { tip ->
                                                Text(
                                                    text = "• $tip",
                                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                                    color = Color.LightGray,
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapScreen(viewModel: SurvivalViewModel) {
    val waypoints by viewModel.waypoints.collectAsStateWithLifecycle()
    val isFormOpen by viewModel.isWpFormOpen.collectAsStateWithLifecycle()

    val addWpName by viewModel.addWpName.collectAsStateWithLifecycle()
    val addWpType by viewModel.addWpType.collectAsStateWithLifecycle()
    val addWpLat by viewModel.addWpLat.collectAsStateWithLifecycle()
    val addWpLng by viewModel.addWpLng.collectAsStateWithLifecycle()
    val addWpDesc by viewModel.addWpDesc.collectAsStateWithLifecycle()

    var isWpSyncing by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1500)
        isWpSyncing = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RADAR GPS DE CACHÉ OFFLINE",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CyberGreen
                )
                Text(
                    text = "Mapa de waypoints y caches tácticos guardados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }
            IconButton(
                onClick = { viewModel.isWpFormOpen.value = !isFormOpen },
                modifier = Modifier.background(CyberGreen, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = if (isFormOpen) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Nuevo Waypoint",
                    tint = SlateCharcoal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isFormOpen) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberGreen, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("GUARDAR NUEVO COORDENADA / CACHÉ", style = MaterialTheme.typography.labelLarge, color = CyberGreen)

                    OutlinedTextField(
                        value = addWpName,
                        onValueChange = { viewModel.addWpName.value = it },
                        label = { Text("Nombre del Punto (ej: Caja de Municion)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGreen, focusedTextColor = Color.White
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = addWpLat,
                            onValueChange = { viewModel.addWpLat.value = it },
                            label = { Text("Latitud") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = addWpLng,
                            onValueChange = { viewModel.addWpLng.value = it },
                            label = { Text("Longitud") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                        )
                    }

                    // Type Row Selection
                    Text("TIPO DE INTERÉS:", style = MaterialTheme.typography.labelSmall, color = GrayText)
                    val types = listOf("Refugio", "Pozo de Agua", "Suministros", "Punto de Extracción", "Peligro")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        types.take(3).forEach { t ->
                            val isSel = addWpType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) CyberGreen else SurfaceMedium, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.addWpType.value = t }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(t, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = if (isSel) SlateCharcoal else Color.White)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = addWpDesc,
                        onValueChange = { viewModel.addWpDesc.value = it },
                        label = { Text("Descripción / Código Secreto") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                    )

                    Button(
                        onClick = { viewModel.createWaypoint() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SlateCharcoal),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("REGISTRAR REGISTRO COORD.", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Radar Canvas Grid Simulator representing nearby points
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().height(180.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().drawBehind {
                    // Scope Grid drawing
                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(color = CyberGreen.copy(0.15f), style = Stroke(1.dp.toPx()), radius = size.height * 0.4f)
                    drawCircle(color = CyberGreen.copy(0.08f), style = Stroke(1.dp.toPx()), radius = size.height * 0.2f)

                    drawLine(color = CyberGreen.copy(0.1f), start = Offset(0f, center.y), end = Offset(size.width, center.y))
                    drawLine(color = CyberGreen.copy(0.1f), start = Offset(center.x, 0f), end = Offset(center.x, size.height))
                },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VISOR MULTIPUNTO ACTIVO (CON CALIBRACIÓN RETRO)",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberGreen.copy(0.7f),
                    modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Draw points on the canvas
                    waypoints.forEachIndexed { idx, wp ->
                        // Calculate offset coordinates relative to some reference
                        val scale = 500f
                        val dx = ((wp.longitude - (-61.1167)) * scale).toFloat()
                        val dy = -((wp.latitude - (-36.2333)) * scale).toFloat()

                        val pointOffset = Offset(
                            (center.x + dx).coerceIn(20f, size.width - 20f),
                            (center.y + dy).coerceIn(20f, size.height - 20f)
                        )

                        // Draw point dot
                        drawCircle(
                            color = when (wp.type) {
                                "Refugio" -> CyberGreen
                                "Pozo de Agua" -> BrightCyan
                                "Suministros" -> WarnAmber
                                else -> AlertRed
                            },
                            radius = 6.dp.toPx(),
                            center = pointOffset
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Waypoints Saved List
        if (isWpSyncing) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    ShimmerSkeletonItem(modifier = Modifier.fillMaxWidth().height(70.dp))
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(waypoints) { wp ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                color = when (wp.type) {
                                                    "Refugio" -> CyberGreen
                                                    "Pozo de Agua" -> BrightCyan
                                                    "Suministros" -> WarnAmber
                                                    else -> AlertRed
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = wp.name,
                                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 15.sp),
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "${wp.latitude}° Lat, ${wp.longitude}° Lng",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrightCyan
                                )
                                Text(
                                    text = wp.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            IconButton(onClick = { viewModel.removeWaypoint(wp) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = AlertRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransmissionsScreen(viewModel: SurvivalViewModel) {
    val transmissions by viewModel.transmissions.collectAsStateWithLifecycle()
    val keyState by viewModel.cipherKey.collectAsStateWithLifecycle()
    val textInput by viewModel.cipherTextInput.collectAsStateWithLifecycle()
    val outputState by viewModel.cipherTextOutput.collectAsStateWithLifecycle()

    val textToMorse by viewModel.textToMorseInput.collectAsStateWithLifecycle()
    val morseResult by viewModel.textToMorseOutput.collectAsStateWithLifecycle()

    var comSubTab by remember { mutableStateOf("cifrado") } // "cifrado", "morse"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "COMUNICACIÓN TÁCTICA SECURE",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberGreen
        )
        Text(
            text = "Módulos de criptografía Vigenère y codificación Morse militar local",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayText
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sub-tabs
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { comSubTab = "cifrado" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (comSubTab == "cifrado") CyberGreen else SurfaceDark,
                    contentColor = if (comSubTab == "cifrado") SlateCharcoal else Color.White
                ),
                modifier = Modifier.weight(1f).border(1.dp, DarkBorder)
            ) {
                Text("CIFRADOR VIGENÈRE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = { comSubTab = "morse" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (comSubTab == "morse") CyberGreen else SurfaceDark,
                    contentColor = if (comSubTab == "morse") SlateCharcoal else Color.White
                ),
                modifier = Modifier.weight(1f).border(1.dp, DarkBorder)
            ) {
                Text("CÓDIGO MORSE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (comSubTab == "cifrado") {
            // Vigenere Box
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ALGORITMO ESTANCO NOMAD VIGENÈRE", style = MaterialTheme.typography.labelLarge, color = CyberGreen)

                    OutlinedTextField(
                        value = keyState,
                        onValueChange = { viewModel.cipherKey.value = it },
                        label = { Text("CLAVE SECRETA (Passkey de Enlace)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                    )

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { viewModel.cipherTextInput.value = it },
                        label = { Text("Texto en claro / Mensaje para codificar") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.transmitEncryptedMessage() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SlateCharcoal),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("CIFRAR & AFSK TX", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.transmitSstvCoordinateBeacon() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightCyan, contentColor = SlateCharcoal),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text("BALIZA SSTV", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.cipherTextInput.value = ""
                                viewModel.cipherTextOutput.value = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceMedium),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text("CLEAR", color = CyberGreen, fontSize = 11.sp)
                        }
                    }

                    if (outputState.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateCharcoal)
                                .border(1.dp, BrightCyan, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("MENSAJE RESULTADO CIFRADO:", style = MaterialTheme.typography.labelSmall, color = BrightCyan)
                                Text(
                                    text = outputState,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Historico
            Text("HISTÓRICO DE EMISIONES CRIPTOGRÁFICAS", style = MaterialTheme.typography.labelSmall, color = GrayText)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(transmissions) { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = log.protocol.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberGreen
                                )
                                IconButton(onClick = { viewModel.deleteTransmissionRecord(log) }) {
                                    Icon(Icons.Default.Delete, "Borrar", tint = AlertRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = "CIPHERTEXT: ${log.cipherText}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "TEXT DECODER: ${log.rawText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayText
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CLAVE: ${log.cipherKey}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrightCyan
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Cargar en Cifrador",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = WarnAmber,
                                    modifier = Modifier.clickable {
                                        viewModel.cipherKey.value = log.cipherKey
                                        viewModel.cipherTextInput.value = log.rawText
                                        viewModel.cipherTextOutput.value = log.cipherText
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Morse Translator Box
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("TRADUCCIÓN DE LUZ EN MORSE MILITAR", style = MaterialTheme.typography.labelLarge, color = CyberGreen)

                    OutlinedTextField(
                        value = textToMorse,
                        onValueChange = { viewModel.onTranslateTextToMorse(it) },
                        label = { Text("Escribe una frase (ej: SOS EMERGENCIA)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                    )

                    if (morseResult.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateCharcoal)
                                .border(1.dp, CyberGreen, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("CÓDIGO DE TRADUCCIÓN:", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
                                Text(
                                    text = morseResult,
                                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace),
                                    color = WarnAmber
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.startMorseFlashing(morseResult) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarnAmber, contentColor = SlateCharcoal),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(48.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Flash")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EMITIR RÁFAGA LUMINOSA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryScreen(viewModel: SurvivalViewModel) {
    val items by viewModel.inventoryItems.collectAsStateWithLifecycle()
    val isFormOpen by viewModel.isInvFormOpen.collectAsStateWithLifecycle()

    val addName by viewModel.addInvName.collectAsStateWithLifecycle()
    val addCategory by viewModel.addInvCategory.collectAsStateWithLifecycle()
    val addQty by viewModel.addInvQty.collectAsStateWithLifecycle()

    var selectedCategoryFilter by remember { mutableStateOf("Mochila (BOB)") }
    val categories = listOf("Mochila (BOB)", "Alimentos/Despensa", "Primeros Auxilios", "Herramientas/Comms")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CONTROL DE INVENTARIOS BOB",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CyberGreen
                )
                Text(
                    text = "Chequeo ágil de mochilas de evacuación y recursos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayText
                )
            }
            IconButton(
                onClick = { viewModel.isInvFormOpen.value = !isFormOpen },
                modifier = Modifier.background(CyberGreen, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = if (isFormOpen) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Añadir Item",
                    tint = SlateCharcoal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inventory addition form
        if (isFormOpen) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberGreen, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("AÑADIR EQUIPO O RECURSOS AL STOCK", style = MaterialTheme.typography.labelLarge, color = CyberGreen)

                    OutlinedTextField(
                        value = addName,
                        onValueChange = { viewModel.addInvName.value = it },
                        label = { Text("Nombre del Suministro") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = addQty,
                            onValueChange = { viewModel.addInvQty.value = it },
                            label = { Text("Cantidad") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White)
                        )

                        // Selector
                        Box(
                            modifier = Modifier
                                .weight(2.5f)
                                .align(Alignment.CenterVertically)
                        ) {
                            // Simple dropdown mock/sim
                            Column {
                                Text("Categoría:", style = MaterialTheme.typography.labelSmall, color = GrayText)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SurfaceMedium, RoundedCornerShape(8.dp))
                                        .clickable {
                                            // Toggle through selection
                                            val currentIdx = categories.indexOf(addCategory)
                                            val nextIdx = (currentIdx + 1) % categories.size
                                            viewModel.addInvCategory.value = categories[nextIdx]
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(addCategory, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    Icon(Icons.Default.Refresh, "Toggle", tint = CyberGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.addInventoryItem() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SlateCharcoal),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("AÑADIR SUMINISTRO", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Category Filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategoryFilter == cat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) CyberGreen else SurfaceDark, RoundedCornerShape(8.dp))
                        .clickable { selectedCategoryFilter = cat }
                        .padding(8.dp)
                        .border(1.dp, if (isSelected) CyberGreen else DarkBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.substringBefore(" (").substringBefore("/"),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isSelected) SlateCharcoal else Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar metric
        val catFilteredItems = items.filter { it.category == selectedCategoryFilter }
        val lenTotal = catFilteredItems.size
        val lenPacked = catFilteredItems.filter { it.isPacked }.size
        val percentage = if (lenTotal > 0) (lenPacked.toFloat() / lenTotal.toFloat()) else 0f

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ESTADO DE COMPACTACIÓN:", style = MaterialTheme.typography.labelSmall, color = GrayText)
                    Text("${(percentage * 100).toInt()}% EMPACADO", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = percentage,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = CyberGreen,
                    trackColor = SurfaceMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vertical List
        if (catFilteredItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Esta categoría está vacía. Añade nuevos suministros arriba.",
                    color = GrayText,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(catFilteredItems) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        modifier = Modifier.fillMaxWidth().border(1.dp, if (item.isPacked) CyberGreen.copy(0.4f) else DarkBorder, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = item.isPacked,
                                    onCheckedChange = { viewModel.toggleItemPacked(item) },
                                    colors = CheckboxDefaults.colors(checkedColor = CyberGreen, checkmarkColor = SlateCharcoal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (item.isPacked) FontWeight.Normal else FontWeight.Bold
                                        ),
                                        color = if (item.isPacked) Color.Gray else Color.White
                                    )
                                    Text(
                                        text = "CANTIDAD REQUERIDA: ${item.quantity}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrightCyan
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.deleteItem(item) }) {
                                Icon(Icons.Default.Delete, "Borrar", tint = AlertRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Simple SelectionContainer emulation fallback for compiler robustness
@Composable
fun SelectionContainer(content: @Composable () -> Unit) {
    Box { content() }
}

@Composable
fun CrisisCalculatorScreen(viewModel: SurvivalViewModel) {
    var activeSubModule by remember { mutableStateOf("fallout") } // "fallout", "shielding", "dehydration", "checklists"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "MATRIZ DE FÍSICA DE CATÁSTROFE",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberGreen
        )
        Text(
            text = "Modelamiento predictivo y blindajes reactivos de campo",
            style = MaterialTheme.typography.bodyMedium,
            color = GrayText
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sub Selector Scrollable Row of Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val subTabs = listOf(
                "fallout" to "FALLOUT EXPANSIVO",
                "shielding" to "BLINDAJE EMP",
                "dehydration" to "PÉRDIDA HÍDRICA",
                "medicina" to "TRIAJE & CLORO g/L",
                "solar_astro" to "SOLAR 12V & ASTRO",
                "checklists" to "PROTOCOLO ACME"
            )
            subTabs.forEach { (id, label) ->
                val isSelected = activeSubModule == id
                Box(
                    modifier = Modifier
                        .background(if (isSelected) CyberGreen else SurfaceDark, RoundedCornerShape(8.dp))
                        .clickable { activeSubModule = id }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .border(1.dp, if (isSelected) CyberGreen else DarkBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = if (isSelected) SlateCharcoal else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Views
        when (activeSubModule) {
            "fallout" -> NuclearCalculatorsView()
            "shielding" -> EmpShieldingView()
            "dehydration" -> WaterSurvivalView()
            "medicina" -> MedicalAndChlorinationView(viewModel)
            "solar_astro" -> SolarAndCelestialView()
            "checklists" -> AdvancedChecklistsView()
        }
    }
}

@Composable
fun MedicalAndChlorinationView(viewModel: SurvivalViewModel) {
    var waterLiters by remember { mutableStateOf("20.0") }
    var bleachConc by remember { mutableStateOf(55) }
    var isTurbid by remember { mutableStateOf(false) }
    var burnWeightKg by remember { mutableStateOf("75.0") }
    var burnTbsa by remember { mutableStateOf("20.0") }
    var medWeightKg by remember { mutableStateOf("25.0") }
    var selectedMed by remember { mutableStateOf("Paracetamol") }

    val chlorResult = CrisisCalculators.calculateWaterChlorination(
        liters = waterLiters.replace(',', '.').toDoubleOrNull() ?: 20.0,
        bleachConcentrationGramsPerLiter = bleachConc,
        isTurbidOrCold = isTurbid
    )
    val orsResult = CrisisCalculators.calculateWhoOrsFormula(1.0)
    val parkland = CrisisCalculators.calculateParklandBurns(
        weightKg = burnWeightKg.replace(',', '.').toDoubleOrNull() ?: 75.0,
        tbsaPercent = burnTbsa.replace(',', '.').toDoubleOrNull() ?: 20.0
    )
    val medDose = CrisisCalculators.calculateEmergencyMedDose(
        medicationKey = selectedMed,
        weightKg = medWeightKg.replace(',', '.').toDoubleOrNull() ?: 25.0,
        isPediatric = (medWeightKg.replace(',', '.').toDoubleOrNull() ?: 25.0) < 40.0
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberGreen, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("POTABILIZACIÓN EXACTA POR CONCENTRACIÓN (g Cl/L)", style = MaterialTheme.typography.titleMedium, color = CyberGreen)
                    OutlinedTextField(
                        value = waterLiters,
                        onValueChange = { waterLiters = it },
                        label = { Text("Volumen de Agua (Litros)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(25, 40, 55, 80).forEach { conc ->
                            Button(
                                onClick = { bleachConc = conc },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (bleachConc == conc) CyberGreen else SurfaceMedium,
                                    contentColor = if (bleachConc == conc) SlateCharcoal else Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("$conc g/L", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Agua Turbia o Muy Fría (Doble Dosis 4 mg/L)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                        Switch(checked = isTurbid, onCheckedChange = { isTurbid = it })
                    }
                    Text(chlorResult.instructions, color = BrightCyan, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SUERO ORAL OMS & QUEMADURAS PARKLAND", style = MaterialTheme.typography.titleMedium, color = WarnAmber)
                    Text("SRO OMS (por 1 Litro): ${orsResult.saltGrams} g Sal (1/2 cdta rasa) + ${orsResult.sugarGrams} g Azúcar (6 cdtas rasas).", color = Color.White)
                    Text(orsResult.clinicalWarning, color = AlertRed, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = burnWeightKg,
                            onValueChange = { burnWeightKg = it },
                            label = { Text("Peso Paciente (kg)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = burnTbsa,
                            onValueChange = { burnTbsa = it },
                            label = { Text("% Superficie Quemada") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    Text(parkland.clinicalNote, color = CyberGreen, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("DOSIFICACIÓN DE EMERGENCIA POR PESO (mg/kg)", style = MaterialTheme.typography.titleMedium, color = BrightCyan)
                    OutlinedTextField(
                        value = medWeightKg,
                        onValueChange = { medWeightKg = it },
                        label = { Text("Peso Corporal (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Paracetamol", "Ibuprofeno", "Amoxicilina", "Doxiciclina").forEach { med ->
                            Button(
                                onClick = { selectedMed = med },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedMed == med) BrightCyan else SurfaceMedium,
                                    contentColor = if (selectedMed == med) SlateCharcoal else Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(med.take(6), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(medDose.guidanceAndWarnings, color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = { viewModel.speakProtocolHandsFree(medDose.guidanceAndWarnings) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SlateCharcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("LEER PROTOCOLO EN VOZ ALTA (MANOS LIBRES)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SolarAndCelestialView() {
    var dailyWhStr by remember { mutableStateOf("600.0") }
    var autonomyDaysStr by remember { mutableStateOf("2") }
    var isLiFePO4 by remember { mutableStateOf(true) }
    var hvlThicknessCm by remember { mutableStateOf("30.0") }
    var hvlMaterial by remember { mutableStateOf("Tierra") }

    val solar = CrisisCalculators.calculateSolarOffGridSystem(
        dailyWh = dailyWhStr.replace(',', '.').toDoubleOrNull() ?: 600.0,
        autonomyDays = autonomyDaysStr.toIntOrNull() ?: 2,
        systemVoltage = 12,
        isLiFePO4 = isLiFePO4
    )
    val gammaHvl = CrisisCalculators.calculateGammaHvlShielding(
        initialDoseRate = 100.0,
        material = hvlMaterial,
        thicknessCm = hvlThicknessCm.replace(',', '.').toDoubleOrNull() ?: 30.0
    )
    val celestial = CrisisCalculators.calculateCelestialNavigation(
        latitude = -36.2333,
        longitude = -61.1167,
        dayOfYear = 190,
        localSolarHour = 12.0
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberGreen, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("DIMENSIONAMIENTO SOLAR OFF-GRID 12V & CABLEADO", style = MaterialTheme.typography.titleMedium, color = CyberGreen)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dailyWhStr,
                            onValueChange = { dailyWhStr = it },
                            label = { Text("Consumo Diario (Wh)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = autonomyDaysStr,
                            onValueChange = { autonomyDaysStr = it },
                            label = { Text("Días sin Sol") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Química LiFePO4 (80% DoD) vs Plomo-Ácido (50% DoD)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                        Switch(checked = isLiFePO4, onCheckedChange = { isLiFePO4 = it })
                    }
                    Text("Banco Baterías 12V: ${String.format(Locale.US, "%.0f", solar.batteryBankAh)} Ah | Paneles: ${String.format(Locale.US, "%.0f", solar.solarArrayWattsPeak)} Wp | Regulador: ${String.format(Locale.US, "%.0f", solar.chargeControllerAmps)} A | Cable Mínimo: ${solar.recommendedAwg}", color = BrightCyan)
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("BLINDAJE GAMMA (CAPA HEMIRREDUCTORA HVL)", style = MaterialTheme.typography.titleMedium, color = WarnAmber)
                    OutlinedTextField(
                        value = hvlThicknessCm,
                        onValueChange = { hvlThicknessCm = it },
                        label = { Text("Espesor del Material (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Plomo", "Acero", "Hormigón", "Tierra", "Agua").forEach { mat ->
                            Button(
                                onClick = { hvlMaterial = mat },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (hvlMaterial == mat) WarnAmber else SurfaceMedium,
                                    contentColor = if (hvlMaterial == mat) SlateCharcoal else Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(mat.take(5), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text("HVL $hvlMaterial = ${gammaHvl.hvlCm} cm | Radiación Bloqueada: ${String.format(Locale.US, "%.1f", gammaHvl.blockedPercent)}%\n${gammaHvl.recommendation}", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("NAVEGACIÓN ASTRONÓMICA SIN GPS (CRUZ DEL SUR / SOL)", style = MaterialTheme.typography.titleMedium, color = CyberGreen)
                    Text(celestial.hemisphereGuide, color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun NuclearCalculatorsView() {
    var yieldKt by remember { mutableStateOf(300.0) }
    var distanceKm by remember { mutableStateOf(15.0) }
    var initialRadR1 by remember { mutableStateOf(300.0) }
    var decayHours by remember { mutableStateOf(7.0) }

    val blast = CrisisCalculators.calculateBlastEffects(yieldKt, distanceKm)
    val decayRadVal = CrisisCalculators.calculateFalloutDecay(initialRadR1, decayHours)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("SIMULADOR FÍSICO DE DETONACIÓN (CENTRO CIV)", style = MaterialTheme.typography.titleLarge, color = CyberGreen)
                    Text("Cálculo aproximado de onda expansiva, calor directo e impulsos.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    // Yield input
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Potencia Detonación (KT): ", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text("${yieldKt.toInt()} Kilotones", style = MaterialTheme.typography.titleMedium, color = WarnAmber, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = yieldKt.toFloat(),
                        onValueChange = { yieldKt = it.toDouble().coerceIn(1.0, 5000.0) },
                        valueRange = 1f..5000f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    // Distance input
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Distancia Hipocentro (km): ", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text(String.format("%.1f km", distanceKm), style = MaterialTheme.typography.titleMedium, color = BrightCyan, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = distanceKm.toFloat(),
                        onValueChange = { distanceKm = it.toDouble().coerceIn(0.5, 100.0) },
                        valueRange = 0.5f..100f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("CONSECUENCIAS EN EL PUNTO DE IMPACTO:", style = MaterialTheme.typography.labelSmall, color = GrayText)

                    // Overpressure
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Presión de Onda Expansiva (Overpressure):", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.2f PSI", blast.overpressurePsi), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (blast.overpressurePsi > 2.0) AlertRed else CyberGreen)
                    }

                    // Threat level tag
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = when (blast.hazardColor) {
                                    "Red" -> AlertRed.copy(0.15f)
                                    "Orange" -> WarnAmber.copy(0.15f)
                                    "Yellow" -> Color.Yellow.copy(0.1f)
                                    else -> DeepOlive.copy(0.3f)
                                }
                            )
                            .border(
                                1.dp,
                                when (blast.hazardColor) {
                                    "Red" -> AlertRed
                                    "Orange" -> WarnAmber
                                    "Yellow" -> Color.Yellow
                                    else -> CyberGreen
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = blast.hazardLevel.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = when (blast.hazardColor) {
                                "Red" -> AlertRed
                                "Orange" -> WarnAmber
                                "Yellow" -> Color.Yellow
                                else -> CyberGreen
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Heat Radiation & Arrival Time
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tiempo llegada Onda de Sonido:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.1f Segundos", blast.arrivalTimeSeconds), style = MaterialTheme.typography.bodyMedium, color = BrightCyan, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Radiación Térmica Directa:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.1f cal/cm²", blast.thermalRadCalCm2), style = MaterialTheme.typography.bodyMedium, color = WarnAmber, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Severidad Quemaduras Térmicas:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(blast.burnSeverity, style = MaterialTheme.typography.bodySmall, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Radio de Interferencia del Pulso EMP:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.1f km", blast.empRadiusKm), style = MaterialTheme.typography.bodyMedium, color = BrightCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("DECLINACIÓN RAD GLOBAL (7:10 RULE)", style = MaterialTheme.typography.titleLarge, color = CyberGreen)
                    Text("Cálculo exponencial del decaimiento radiactivo de fallout seco.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Tasa Radiación Hora 1 (R/h):", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text("${initialRadR1.toInt()} R/h", style = MaterialTheme.typography.titleMedium, color = AlertRed, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = initialRadR1.toFloat(),
                        onValueChange = { initialRadR1 = it.toDouble().coerceIn(10.0, 2000.0) },
                        valueRange = 10f..2000f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Tiempo Transcurrido (Horas):", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text("${decayHours.toInt()} Horas", style = MaterialTheme.typography.titleMedium, color = BrightCyan, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = decayHours.toFloat(),
                        onValueChange = { decayHours = it.toDouble().coerceIn(1.0, 168.0) },
                        valueRange = 1f..168f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    Divider(color = DarkBorder)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateCharcoal)
                            .border(1.dp, CyberGreen.copy(0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TASA RESIDUAL ESTIMADA:", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
                            Text(String.format("%.3f R/h", decayRadVal), style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .background(if (decayRadVal > 50.0) AlertRed.copy(0.2f) else DeepOlive)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .border(1.dp, if (decayRadVal > 50.0) AlertRed else CyberGreen, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = if (decayRadVal > 50.0) "EXTREMO PELIGRO" else "TOLERABLE",
                                color = if (decayRadVal > 50.0) AlertRed else CyberGreen,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Predefined decay tables
                    Text("TABLA DE DECAIMIENTO REGULATORIO (7:10)", style = MaterialTheme.typography.labelSmall, color = GrayText)
                    val timeline = CrisisCalculators.getDecayTimeline(initialRadR1)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceMedium)
                            .padding(8.dp)
                    ) {
                        timeline.forEach { (h, value) ->
                            val daysLabel = when (h) {
                                1 -> "1 Hora"
                                2 -> "2 Horas"
                                7 -> "7 Horas (Caída 90%)"
                                24 -> "24 Horas"
                                48 -> "48 Horas (~2 días)"
                                168 -> "1 Semana"
                                336 -> "2 Semanas"
                                else -> "$h Horas"
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(daysLabel, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                Text(String.format("%.2f R/h", value), style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = if (value > 50.0) AlertRed else CyberGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmpShieldingView() {
    var thicknessMm by remember { mutableStateOf(1.5) }
    var materialSelected by remember { mutableStateOf("Cobre") }
    val materials = listOf("Cobre", "Aluminio", "Acero")

    val shield = CrisisCalculators.calculateEmpShielding(thicknessMm, materialSelected)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("CALCULADORA DE BLINDAJE ELECTROMAGNÉTICO", style = MaterialTheme.typography.titleLarge, color = CyberGreen)
                    Text("Valida la atenuación espectral de tus cámaras y jaulas de Faraday improvisadas de campaña.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    // Material Selection Buttons
                    Text("Material del Blindaje Radial:", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        materials.forEach { mat ->
                            val isSel = materialSelected == mat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) CyberGreen else SurfaceMedium, RoundedCornerShape(8.dp))
                                    .clickable { materialSelected = mat }
                                    .padding(vertical = 12.dp)
                                    .border(1.dp, if (isSel) CyberGreen else DarkBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(mat, color = if (isSel) SlateCharcoal else Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    // Thickness Slider
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Grosor de Placa Metal (mm):", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text(String.format("%.2f mm", thicknessMm), style = MaterialTheme.typography.titleMedium, color = BrightCyan, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = thicknessMm.toFloat(),
                        onValueChange = { thicknessMm = it.toDouble().coerceIn(0.1, 10.0) },
                        valueRange = 0.1f..10.0f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    Divider(color = DarkBorder)

                    Text("COEFICIENTE DE PROTECCIÓN RESILIENTE:", style = MaterialTheme.typography.labelSmall, color = GrayText)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Atenuación Estructural:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.1f dB", shield.attenuationDb), style = MaterialTheme.typography.bodyLarge, color = CyberGreen, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Energía de Onda Absorbida:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.6f %%", shield.energyBlockedPercent), style = MaterialTheme.typography.bodyLarge, color = BrightCyan, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = when (shield.assessmentColor) {
                                    "Green" -> DeepOlive.copy(0.3f)
                                    "Yellow" -> WarnAmber.copy(0.1f)
                                    else -> AlertRed.copy(0.15f)
                                }
                            )
                            .border(
                                1.dp,
                                when (shield.assessmentColor) {
                                    "Green" -> CyberGreen
                                    "Yellow" -> WarnAmber
                                    else -> AlertRed
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("EVALUACIÓN DE CAMPO:", style = MaterialTheme.typography.labelSmall, color = GrayText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = shield.safetyAssessment,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = when (shield.assessmentColor) {
                                    "Green" -> CyberGreen
                                    "Yellow" -> WarnAmber
                                    else -> AlertRed
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaterSurvivalView() {
    var tempC by remember { mutableStateOf(35.0) }
    var activityLevel by remember { mutableStateOf("Reposo") }
    var currentLitres by remember { mutableStateOf(5.0) }
    val activities = listOf("Reposo", "Caminata Activa", "Físico Extremo")

    val stat = CrisisCalculators.calculateDehydration(tempC, activityLevel, currentLitres)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ESTIMADOR DE SUDORACIÓN & DESIERTO", style = MaterialTheme.typography.titleLarge, color = CyberGreen)
                    Text("Calcula la pérdida hídrica diaria por sudoración y la reserva necesaria en sequías extremas.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    // Temp Slider
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Temperatura Ambiente (°C):", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text("${tempC.toInt()} °C", style = MaterialTheme.typography.titleMedium, color = if (tempC > 40.0) AlertRed else CyberGreen, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = tempC.toFloat(),
                        onValueChange = { tempC = it.toDouble().coerceIn(15.0, 55.0) },
                        valueRange = 15f..55f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    // Water supply input
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reserva Agua Actual (Litros):", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text(String.format("%.1f L", currentLitres), style = MaterialTheme.typography.titleMedium, color = BrightCyan, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = currentLitres.toFloat(),
                        onValueChange = { currentLitres = it.toDouble().coerceIn(0.0, 50.0) },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    // Activity Level buttons
                    Text("Nivel de Gasto de Ignición:", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        activities.forEach { act ->
                            val isSel = activityLevel == act
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) CyberGreen else SurfaceMedium, RoundedCornerShape(8.dp))
                                    .clickable { activityLevel = act }
                                    .padding(vertical = 10.dp)
                                    .border(1.dp, if (isSel) CyberGreen else DarkBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = act,
                                    color = if (isSel) SlateCharcoal else Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                )
                            }
                        }
                    }

                    Divider(color = DarkBorder)

                    Text("COMPORTAMIENTO METABÓLICO DE CRISIS:", style = MaterialTheme.typography.labelSmall, color = GrayText)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Sudoración y Pérdida diaria de Agua:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.2f Litros/día", stat.waterNeededLitres), style = MaterialTheme.typography.bodyMedium, color = WarnAmber, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Duración de tu Reserva Existente:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.1f Días", stat.daysWithSupply), style = MaterialTheme.typography.bodyMedium, color = CyberGreen, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tiempo Límite antes de Colapso Símil:", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(String.format("%.1f Horas", stat.survivalHoursWithoutWater), style = MaterialTheme.typography.bodyMedium, color = AlertRed, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = when (stat.statusColor) {
                                    "Green" -> DeepOlive.copy(0.3f)
                                    "Yellow" -> WarnAmber.copy(0.1f)
                                    "Orange" -> WarnAmber.copy(0.2f)
                                    else -> AlertRed.copy(0.15f)
                                }
                            )
                            .border(
                                1.dp,
                                when (stat.statusColor) {
                                    "Green" -> CyberGreen
                                    "Yellow" -> Color.Yellow
                                    "Orange" -> WarnAmber
                                    else -> AlertRed
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stat.statusImpact.uppercase(),
                            color = when (stat.statusColor) {
                                "Green" -> CyberGreen
                                "Yellow" -> Color.Yellow
                                "Orange" -> WarnAmber
                                else -> AlertRed
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdvancedChecklistsView() {
    var checklist1 by remember { mutableStateOf(false) }
    var checklist2 by remember { mutableStateOf(false) }
    var checklist3 by remember { mutableStateOf(false) }
    var checklist4 by remember { mutableStateOf(false) }
    var checklist5 by remember { mutableStateOf(false) }
    var checklist6 by remember { mutableStateOf(false) }
    var checklist7 by remember { mutableStateOf(false) }
    var checklist8 by remember { mutableStateOf(false) }
    var checklist9 by remember { mutableStateOf(false) }
    var checklist10 by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("CÚPULA DEFENSIVA MULTI-RIESGO", style = MaterialTheme.typography.titleLarge, color = CyberGreen)
                    Text("Lista estricta de acciones físicas prioritarias listas para despliegue ante alertas o catástrofes.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    Divider(color = DarkBorder)

                    // CME / EMP Action
                    Text("A) AMENAZA SOLAR G5 y EMP TÁCTICO", style = MaterialTheme.typography.labelSmall, color = WarnAmber)
                    ChecklistItemRow("1. Envolver transmisores, baterías y radios en film aislante y luego en triple papel de aluminio sin fugas radiativas.", checklist1) { checklist1 = it }
                    ChecklistItemRow("2. Desconectar líneas físicas externas de antena coaxial y cables LAN Ethernet para evitar inducción térmica de retorno.", checklist2) { checklist2 = it }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Nuclear fallout Action
                    Text("B) ESCENARIO INTEGRAL DE GUERRA NUCLEAR (FALLOUT)", style = MaterialTheme.typography.labelSmall, color = AlertRed)
                    ChecklistItemRow("3. Desplegar filtros de aire de búnker / habitación segura con mínimo de 15 cm de carbón activo secuestrante y HEPA.", checklist3) { checklist3 = it }
                    ChecklistItemRow("4. Asegurar acopio inerte de agua de por lo menos 3 litros por persona / día, sellando botes contra lluvia ácida.", checklist4) { checklist4 = it }
                    ChecklistItemRow("5. Medir radiación real en decaimiento regla 7:10 de fisión. No abandonar el aposento seguro antes del día 14.", checklist5) { checklist5 = it }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bio Hazard Action
                    Text("C) CRISIS BIOLÓGICA & AGENTES PATÓGENOS", style = MaterialTheme.typography.labelSmall, color = BrightCyan)
                    ChecklistItemRow("6. Sellar umbral del aposento habitable con film continuo de polietileno transparente de 6 milésimas sujeto con cinta gris.", checklist6) { checklist6 = it }
                    ChecklistItemRow("7. Preparar dilución fresca de cloro e hipoclorito de sodio al 0.5% en rociador manual para purga de calzado y trajes Tyvek.", checklist7) { checklist7 = it }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Drought, collapse Action
                    Text("D) INUNDACIONES DE SECTOR & SEQUÍAS", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
                    ChecklistItemRow("8. Empacar kits electrónicos estancos BOB del fondo. No marchar en cauces con acumulo de agua de más de 15 cm.", checklist8) { checklist8 = it }
                    ChecklistItemRow("9. Señalizar tejados con regla de 3 fuegos o señales del heliógrafo solar activo directo.", checklist9) { checklist9 = it }
                    ChecklistItemRow("10. Instalar destilador solar secundario en subsuelo húmedo para recuperar transpiración terrestre potable.", checklist10) { checklist10 = it }
                }
            }
        }
    }
}

@Composable
fun ChecklistItemRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = CyberGreen, checkmarkColor = SlateCharcoal, uncheckedColor = DarkBorder)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = if (isChecked) Color.Gray else Color.White)
    }
}

@Composable
fun InteractiveRepoTool(repoName: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceMedium),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberGreen.copy(0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Build, "Herramienta", tint = CyberGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MÓDULO INTERACTIVO DE INTEGRACIÓN NOMAD",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyberGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            when {
                repoName.contains("Project Nomad", ignoreCase = true) -> {
                    var freqStr by remember { mutableStateOf("915.0") }
                    val freq = freqStr.replace(',', '.').toDoubleOrNull() ?: 915.0
                    val quarterWave = if (freq > 0) 71.25 / freq else 0.0
                    val halfWave = if (freq > 0) 142.5 / freq else 0.0

                    Text("Calculador de Resonancia de Antenas en Dipolo de Campaña", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Ingresa la frecuencia en MHz para obtener la medida física exacta del cable de cobre para tu transceptor.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    OutlinedTextField(
                        value = freqStr,
                        onValueChange = { freqStr = it },
                        label = { Text("Frecuencia en MHz") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White, unfocusedLabelColor = GrayText, focusedLabelColor = CyberGreen),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            modifier = Modifier.weight(1f).border(1.dp, DarkBorder, RoundedCornerShape(4.dp)).padding(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Largo 1/4 Onda (Monopolo)", style = MaterialTheme.typography.labelSmall, color = GrayText, textAlign = TextAlign.Center)
                                Text(String.format("%.2f cm", quarterWave * 100), style = MaterialTheme.typography.titleMedium, color = CyberGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            modifier = Modifier.weight(1f).border(1.dp, DarkBorder, RoundedCornerShape(4.dp)).padding(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Largo 1/2 Onda (Dipolo)", style = MaterialTheme.typography.labelSmall, color = GrayText, textAlign = TextAlign.Center)
                                Text(String.format("%.2f cm", halfWave * 100), style = MaterialTheme.typography.titleMedium, color = BrightCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text("💡 Consejo Nomad: Para una antena dipolo perfecta, corta dos ramales de cobre del tamaño de 1/4 de onda y únelos en el núcleo central coaxial.", style = MaterialTheme.typography.bodySmall, color = WarnAmber)
                }

                repoName.contains("Meshtastic", ignoreCase = true) -> {
                    var distanceInput by remember { mutableStateOf("5.0") }
                    var terrainChecked by remember { mutableStateOf(true) }
                    val distance = distanceInput.replace(',', '.').toDoubleOrNull() ?: 5.0
                    val frequencyHz = 0.915 // GHz
                    val fresnelRadius = 17.32 * kotlin.math.sqrt(distance / (4.0 * frequencyHz))

                    Text("Modelador de Línea de Vista y Zona de Fresnel (915 MHz)", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Calcula la altura mínima libre sobre obstáculos para evitar atenuación severa de paquetes LoRa.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Distancia entre Nodos:", style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text(String.format("%.1f km", distance), style = MaterialTheme.typography.titleMedium, color = BrightCyan, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = distance.toFloat(),
                        onValueChange = { distanceInput = String.format(Locale.US, "%.1f", it) },
                        valueRange = 0.5f..40.0f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Terreno Obstruido por Bosque/Área Urbana (+60%)", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Switch(
                            checked = terrainChecked,
                            onCheckedChange = { terrainChecked = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberGreen)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark)
                            .border(1.dp, DarkBorder, RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Radio Zona de Fresnel Central:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(String.format("%.2f Metros", fresnelRadius), style = MaterialTheme.typography.bodyMedium, color = CyberGreen, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Altura de Mástil Recomendada:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                val recommendedHeight = if (terrainChecked) fresnelRadius * 1.6 else fresnelRadius * 1.0
                                Text(String.format("%.1f Metros", recommendedHeight), style = MaterialTheme.typography.bodyMedium, color = WarnAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                repoName.contains("Survival_Kit", ignoreCase = true) -> {
                    var totalCalsStr by remember { mutableStateOf("75000") }
                    var peopleStr by remember { mutableStateOf("3") }
                    var dailyIntakeStr by remember { mutableStateOf("2000") }

                    val totalCals = totalCalsStr.replace(',', '.').toDoubleOrNull() ?: 75000.0
                    val people = peopleStr.replace(',', '.').toDoubleOrNull() ?: 3.0
                    val dailyIntake = dailyIntakeStr.replace(',', '.').toDoubleOrNull() ?: 2000.0

                    val daysOfSupply = if (people * dailyIntake > 0) totalCals / (people * dailyIntake) else 0.0

                    Text("Planificador de Autonomía Calórica de Despensa", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Calcula de forma exacta cuántos días completos resistirá tu despensa inerte según los comensales.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = totalCalsStr,
                            onValueChange = { totalCalsStr = it },
                            label = { Text("Total Calorías", fontSize = 10.sp) },
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White, unfocusedLabelColor = GrayText, focusedLabelColor = CyberGreen),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = peopleStr,
                            onValueChange = { peopleStr = it },
                            label = { Text("Operadores", fontSize = 10.sp) },
                            modifier = Modifier.weight(0.8f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White, unfocusedLabelColor = GrayText, focusedLabelColor = CyberGreen),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = dailyIntakeStr,
                            onValueChange = { dailyIntakeStr = it },
                            label = { Text("Ración/Día", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White, unfocusedLabelColor = GrayText, focusedLabelColor = CyberGreen),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark)
                            .border(1.dp, if (daysOfSupply < 14) AlertRed else CyberGreen, RoundedCornerShape(4.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("AUTONOMÍA TOTAL:", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        Text(String.format("%.1f Días de Alimentos", daysOfSupply), style = MaterialTheme.typography.titleMedium, color = if (daysOfSupply < 14) AlertRed else CyberGreen, fontWeight = FontWeight.Bold)
                    }
                }

                repoName.contains("Kiwix", ignoreCase = true) -> {
                    var selWiki by remember { mutableStateOf(true) }
                    var selWikiMed by remember { mutableStateOf(true) }
                    var selWikiHow by remember { mutableStateOf(true) }
                    var selGutenberg by remember { mutableStateOf(false) }
                    var selMilitary by remember { mutableStateOf(true) }

                    var targetSdGb by remember { mutableStateOf(64f) }

                    val totalGb = (if (selWiki) 52.0 else 0.0) +
                                  (if (selWikiMed) 2.5 else 0.0) +
                                  (if (selWikiHow) 4.0 else 0.0) +
                                  (if (selGutenberg) 12.0 else 0.0) +
                                  (if (selMilitary) 1.5 else 0.0)

                    Text("Planificador de Almacenamiento Kiwix .ZIM", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Selecciona los archivos ZIM que descargarás para calcular el tamaño requerido de tu MicroSD.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selWiki = !selWiki }) {
                            Checkbox(checked = selWiki, onCheckedChange = { selWiki = it }, colors = CheckboxDefaults.colors(checkedColor = CyberGreen))
                            Text("Wikipedia Completa Español (~52.0 GB)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selWikiMed = !selWikiMed }) {
                            Checkbox(checked = selWikiMed, onCheckedChange = { selWikiMed = it }, colors = CheckboxDefaults.colors(checkedColor = CyberGreen))
                            Text("WikiMed (Enciclopedia Médica) (~2.5 GB)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selWikiHow = !selWikiHow }) {
                            Checkbox(checked = selWikiHow, onCheckedChange = { selWikiHow = it }, colors = CheckboxDefaults.colors(checkedColor = CyberGreen))
                            Text("WikiHow (Manuales Prácticos) (~4.0 GB)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selGutenberg = !selGutenberg }) {
                            Checkbox(checked = selGutenberg, onCheckedChange = { selGutenberg = it }, colors = CheckboxDefaults.colors(checkedColor = CyberGreen))
                            Text("Proyecto Gutenberg (~12.0 GB)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selMilitary = !selMilitary }) {
                            Checkbox(checked = selMilitary, onCheckedChange = { selMilitary = it }, colors = CheckboxDefaults.colors(checkedColor = CyberGreen))
                            Text("Manuales Militares de Ingeniería (~1.5 GB)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Capacidad Tarjeta SD:", style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text("${targetSdGb.toInt()} GB", style = MaterialTheme.typography.bodyMedium, color = BrightCyan, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = targetSdGb,
                        onValueChange = { targetSdGb = it },
                        valueRange = 8f..256f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark)
                            .border(1.dp, if (totalGb > targetSdGb) AlertRed else CyberGreen, RoundedCornerShape(4.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Espacio Requerido: ${totalGb} GB", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(
                            text = if (totalGb > targetSdGb) "NO CABE (Excede SD!)" else "APTO (Cabe en SD)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (totalGb > targetSdGb) AlertRed else CyberGreen
                        )
                    }
                }

                repoName.contains("Internet-in-a-Box", ignoreCase = true) -> {
                    var ssid by remember { mutableStateOf("IIAB_NOMAD_WIFI") }
                    var wpa3Enabled by remember { mutableStateOf(false) }

                    Text("Generador de Configuración de Portal Wi-Fi (IIAB)", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Configura un punto de acceso inalámbrico cautivo para compartir el contenido del servidor de forma abierta local.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    OutlinedTextField(
                        value = ssid,
                        onValueChange = { ssid = it },
                        label = { Text("Nombre de Red WiFi (SSID)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White, unfocusedLabelColor = GrayText, focusedLabelColor = CyberGreen)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activar Cifrado WPA (Seguridad)", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Switch(checked = wpa3Enabled, onCheckedChange = { wpa3Enabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = CyberGreen))
                    }

                    Text("/etc/hostapd/hostapd.conf (Valores Generados):", style = MaterialTheme.typography.labelSmall, color = CyberGreen)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark)
                            .border(1.dp, DarkBorder, RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "interface=wlan0\ndriver=nl80211\nssid=$ssid\nhw_mode=g\nchannel=6\n${if (wpa3Enabled) "wpa=2\nwpa_key_mgmt=WPA-PSK\nwpa_passphrase=NOMADTACTICAL" else "auth_algs=1\nwpa=0"}",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = Color.LightGray
                        )
                    }
                    Text("📋 Copia esto en tu placa IIAB para crear la celda de datos descentralizada.", style = MaterialTheme.typography.bodySmall, color = WarnAmber)
                }

                repoName.contains("refugiOS", ignoreCase = true) -> {
                    var targetDisk by remember { mutableStateOf("sdb") }
                    var wipeCycles by remember { mutableStateOf(3f) }

                    Text("Compilador de Borrado Absoluto Forense (Anti-Forense)", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Genera comandos Unix listos para destruir de forma irrecuperable sectores de almacenamiento comprometidos.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    OutlinedTextField(
                        value = targetDisk,
                        onValueChange = { targetDisk = it },
                        label = { Text("Identificador de Disco Objetivo (ej: sdb)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGreen, focusedTextColor = Color.White, unfocusedLabelColor = GrayText, focusedLabelColor = CyberGreen)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ciclos de sobreescritura aleatorios:", style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text("${wipeCycles.toInt()} pasadas", style = MaterialTheme.typography.bodyMedium, color = WarnAmber, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                    }
                    Slider(
                        value = wipeCycles,
                        onValueChange = { wipeCycles = it },
                        valueRange = 1f..10f,
                        colors = SliderDefaults.colors(thumbColor = CyberGreen, activeTrackColor = CyberGreen)
                    )

                    Text("Comando de Destrucción de Cabeceras (Ejecución Terminal):", style = MaterialTheme.typography.labelSmall, color = AlertRed)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark)
                            .border(1.dp, AlertRed, RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "shred -v -u -n ${wipeCycles.toInt()} --random-source=/dev/urandom /dev/$targetDisk",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = AlertRed
                        )
                    }
                    Text("⚠️ ATENCIÓN: Este comando reescribe permanentemente con ruido blanco electromagnético el disco físico seleccionado. Es irreversible.", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                }

                repoName.contains("Doomsday Cyberdeck", ignoreCase = true) -> {
                    var activeBandFilter by remember { mutableStateOf("VHF") }
                    val frequencies = when (activeBandFilter) {
                        "HF" -> listOf(
                            "7.065 MHz" to "Canal de Emergencia CB (Red Terrestre)",
                            "3.750 MHz" to "Frecuencia de Catástrofes LSB Nacional",
                            "14.300 MHz" to "Red de Monitoreo Marítimo Internacional"
                        )
                        "VHF" -> listOf(
                            "121.500 MHz" to "Frecuencia Satelital Aeronáutica Mayday HG",
                            "146.520 MHz" to "Llamada Simplex VHF Radioaficionados",
                            "156.800 MHz" to "Canal 16 de Seguridad Marítima SOS"
                        )
                        "UHF" -> listOf(
                            "446.031 MHz" to "Canal 3 PMR446 (Ayuda local sin licencia)",
                            "462.675 MHz" to "Canal 9 de Conversación General SOS en GMRS"
                        )
                        else -> emptyList()
                    }

                    Text("Co-Procesador de Frecuencias de Monitoreo RTL-SDR", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Configuración de canales del receptor de tu Cyberdeck para captar ráfagas de transmisiones oficiales.", style = MaterialTheme.typography.bodySmall, color = GrayText)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("HF", "VHF", "UHF").forEach { b ->
                            val isSel = activeBandFilter == b
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) CyberGreen else SurfaceDark, RoundedCornerShape(4.dp))
                                    .clickable { activeBandFilter = b }
                                    .padding(vertical = 8.dp)
                                    .border(1.dp, if (isSel) CyberGreen else DarkBorder, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(b, color = if (isSel) SlateCharcoal else Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    Text("Frecuencias Clave a Escuchar:", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
                    Column(
                        modifier = Modifier.fillMaxWidth().background(SurfaceDark).padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        frequencies.forEach { (freq, label) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(freq, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = BrightCyan)
                                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.LightGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                else -> {
                    Text("Mapeo táctico inerte sin submódulo interactivo activo.", style = MaterialTheme.typography.bodySmall, color = GrayText)
                }
            }
        }
    }
}

@Composable
fun SolidarityScreen(viewModel: SurvivalViewModel) {
    val pactoEmpatia by viewModel.pactoEmpatia.collectAsStateWithLifecycle()
    val pactoApoyo by viewModel.pactoApoyo.collectAsStateWithLifecycle()
    val pactoInformacion by viewModel.pactoInformacion.collectAsStateWithLifecycle()
    val pactoComunicaciones by viewModel.pactoComunicaciones.collectAsStateWithLifecycle()
    val pactoVulnerables by viewModel.pactoVulnerables.collectAsStateWithLifecycle()

    val posts by viewModel.solidarityPosts.collectAsStateWithLifecycle()

    val authorInput by viewModel.newPostAuthor.collectAsStateWithLifecycle()
    val msgInput by viewModel.newPostMessage.collectAsStateWithLifecycle()
    val typeInput by viewModel.newPostType.collectAsStateWithLifecycle()
    val regionInput by viewModel.newPostRegion.collectAsStateWithLifecycle()

    val isFormOpen = remember { mutableStateOf(false) }

    val isPledgeSigned = pactoEmpatia && pactoApoyo && pactoInformacion && pactoComunicaciones && pactoVulnerables

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero / Introduction Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Pacto de Unión",
                            tint = CyberGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "PORTAL DE SOLIDARIDAD GLOBAL",
                            style = MaterialTheme.typography.titleLarge,
                            color = CyberGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "La supervivencia individual es frágil. La verdadera resiliencia humana radica en la empatía, la ayuda mutua y la colaboración descentralizada. Este módulo unifica tecnologías abiertas y de radiofrecuencia para construir una red mundial de apoyo en crisis, resistiendo hombro con hombro.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        // Accord / Pact Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isPledgeSigned) CyberGreen else WarnAmber.copy(0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GRAN CONSENSO DE LA HUMANIDAD // COMPROMISO COOPERATIVO",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isPledgeSigned) CyberGreen else WarnAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Firma el pacto voluntario comprometiéndote con los valores supremos de la civilización humana ante cualquier desastre global.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Checklist
                    PledgeItem(
                        label = "Sostener la Empatía y Solidaridad",
                        desc = "Anteponer el cuidado colectivo sobre el pánico individual y el acaparamiento egoísta.",
                        isChecked = pactoEmpatia,
                        onCheckedChange = { viewModel.pactoEmpatia.value = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PledgeItem(
                        label = "Compartir Recursos Vitales",
                        desc = "Habilitar reservas compartidas de agua, fármacos básicos, alimentos y calor humano.",
                        isChecked = pactoApoyo,
                        onCheckedChange = { viewModel.pactoApoyo.value = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PledgeItem(
                        label = "Democratizar la Información Offline",
                        desc = "Convertirse en nodo de conocimiento compartiendo Kiwix y bibliotecas abiertas sin fines de lucro.",
                        isChecked = pactoInformacion,
                        onCheckedChange = { viewModel.pactoInformacion.value = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PledgeItem(
                        label = "Establecer Canales de Emergencia Libres",
                        desc = "Mantener enlaces radiales LoRa/VHF encendidos para rescatar pueblos aislados y esparcir esperanza.",
                        isChecked = pactoComunicaciones,
                        onCheckedChange = { viewModel.pactoComunicaciones.value = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PledgeItem(
                        label = "Salvaguardar a los Sectores Vulnerables",
                        desc = "Defender primero a niños, enfermos, ancianos y animales con compasión incondicional.",
                        isChecked = pactoVulnerables,
                        onCheckedChange = { viewModel.pactoVulnerables.value = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isPledgeSigned) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberGreen.copy(0.1f))
                                .border(1.dp, CyberGreen, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, "Pacto Activo", tint = CyberGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PACTO DE ALIANZA ACTIVO // BEACON HUMANITARIO EMITIENDO",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = CyberGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Estás enlazado con la red de empatía continental. Tu señal de apoyo impulsa la resiliencia.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WarnAmber.copy(0.05f))
                                .border(1.dp, WarnAmber.copy(0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚠️ Firme cada postulado marcando las casillas superiores para activar el faro de Unión Global.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarnAmber
                            )
                        }
                    }
                }
            }
        }

        // Open Source Matrix
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGreen.copy(0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LA SKELETON-MATRIX DE INFRAESTRUCTURA HUMANITARIA",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyberGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "La unión se operacionaliza a través de proyectos abiertos consolidados. Esta app centraliza e integra los conceptos de estas tecnologías:",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TechItem(
                        name = "Kiwix Offline Servers",
                        desc = "Preservación empática de la cultura humana offline. Compresión ZIM de Wikipedia, WikiMed y WikiHow para consultas seguras sin Internet.",
                        icon = Icons.Default.Info,
                        color = BrightCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TechItem(
                        name = "Internet-in-a-Box (IIAB)",
                        desc = "Plataforma física que crea portales cautivos Wi-Fi locales autónomos utilizando dispositivos de bajo consumo para educar comunidades.",
                        icon = Icons.Default.Share,
                        color = CyberGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TechItem(
                        name = "Project Nomad / Meshtastic LoRa",
                        desc = "Mapeo de zonas LoRa y cálculo de antenas dipolo para mantener transmisiones de texto libres y descentralizadas entre comunas.",
                        icon = Icons.Default.Place,
                        color = WarnAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TechItem(
                        name = "Doomsday Cyberdecks & SDR",
                        desc = "Sistemas autoconstruidos optimizados para la escucha de socorros y reportes climáticos regionales a través de radio software (SDR).",
                        icon = Icons.Default.Settings,
                        color = BrightCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TechItem(
                        name = "refugiOS Forensic Safeguards",
                        desc = "Herramientas de borrado seguro forense para proteger bases de datos comunitarias y registros solidarios ante incursiones hostiles.",
                        icon = Icons.Default.Lock,
                        color = AlertRed
                    )
                }
            }
        }

        // Bulletin Board Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TABLERO CIVIL DE AYUDA Y FE",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Button(
                    onClick = { isFormOpen.value = !isFormOpen.value },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isFormOpen.value) SurfaceMedium else CyberGreen, contentColor = SlateCharcoal)
                ) {
                    Icon(
                        imageVector = if (isFormOpen.value) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Publicar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isFormOpen.value) "Cerrar" else "Añadir Mensaje", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isFormOpen.value) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberGreen, RoundedCornerShape(8.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "DIFUNDIR ALERTA O MENSAJE A LA COMUNIDAD MUNDIAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberGreen
                        )

                        OutlinedTextField(
                            value = authorInput,
                            onValueChange = { viewModel.newPostAuthor.value = it },
                            label = { Text("Tu Identidad o Refugio") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGreen, focusedTextColor = Color.White,
                                unfocusedBorderColor = DarkBorder, unfocusedLabelColor = GrayText,
                                focusedLabelColor = CyberGreen
                            )
                        )

                        OutlinedTextField(
                            value = regionInput,
                            onValueChange = { viewModel.newPostRegion.value = it },
                            label = { Text("Ubicación / Territorio (ej: Santiago Centro)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGreen, focusedTextColor = Color.White,
                                unfocusedBorderColor = DarkBorder, unfocusedLabelColor = GrayText,
                                focusedLabelColor = CyberGreen
                            )
                        )

                        OutlinedTextField(
                            value = msgInput,
                            onValueChange = { viewModel.newPostMessage.value = it },
                            label = { Text("Escribe tu propuesta, solicitud de auxilio, o palabras de fe") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGreen, focusedTextColor = Color.White,
                                unfocusedBorderColor = DarkBorder, unfocusedLabelColor = GrayText,
                                focusedLabelColor = CyberGreen
                            )
                        )

                        // Post type selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val types = listOf("Ayuda Colectiva", "Mensaje de Esperanza", "Alerta Sanitaria", "Conocimiento Compartido")
                            types.forEach { t ->
                                val isSelected = typeInput == t
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (isSelected) CyberGreen else SurfaceMedium, RoundedCornerShape(4.dp))
                                        .clickable { viewModel.newPostType.value = t }
                                        .border(1.dp, if (isSelected) CyberGreen else DarkBorder, RoundedCornerShape(4.dp))
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when(t) {
                                            "Ayuda Colectiva" -> "Ayuda"
                                            "Mensaje de Esperanza" -> "Fe"
                                            "Alerta Sanitaria" -> "Salud"
                                            "Conocimiento Compartido" -> "Saber"
                                            else -> t
                                        },
                                        color = if (isSelected) SlateCharcoal else Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.publishSolidarityPost()
                                isFormOpen.value = false
                            },
                            enabled = msgInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SlateCharcoal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Send, "Publicar", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DIFUNDIR EN PACKETS MESH ENCRIPTADOS", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(posts) { post ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = when (post.type) {
                            "Ayuda Colectiva" -> AlertRed.copy(0.4f)
                            "Mensaje de Esperanza" -> CyberGreen.copy(0.4f)
                            "Alerta Sanitaria" -> WarnAmber.copy(0.4f)
                            "Conocimiento Compartido" -> BrightCyan.copy(0.4f)
                            else -> DarkBorder
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.type.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (post.type) {
                                "Ayuda Colectiva" -> AlertRed
                                "Mensaje de Esperanza" -> CyberGreen
                                "Alerta Sanitaria" -> WarnAmber
                                "Conocimiento Compartido" -> BrightCyan
                                else -> Color.LightGray
                            }
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, "Ubicación", tint = GrayText, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(post.region, style = MaterialTheme.typography.labelSmall, color = GrayText)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(post.message, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Por: ${post.author}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GrayText
                        )
                        Row(
                            modifier = Modifier
                                .background(SurfaceMedium, RoundedCornerShape(4.dp))
                                .clickable { viewModel.upvotePost(post) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Favorite, "Apoyo", tint = CyberGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Brindar Apoyo (${post.upvotes})", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PledgeItem(
    label: String,
    desc: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = CyberGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = GrayText)
        }
    }
}

@Composable
fun TechItem(
    name: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, name, tint = color, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
        }
    }
}

@Composable
fun TacticalTooltip(infoText: String) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(start = 4.dp)) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = BrightCyan,
            modifier = Modifier
                .size(13.dp)
                .clickable { expanded = !expanded }
        )
        if (expanded) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceMedium),
                border = BorderStroke(1.dp, BrightCyan),
                modifier = Modifier
                    .padding(top = 18.dp)
                    .width(220.dp)
                    .zIndex(100f)
            ) {
                Text(
                    text = infoText,
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun ShimmerSkeletonItem(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color.Gray.copy(alpha = 0.5f),
        Color.Gray.copy(alpha = 0.1f),
        Color.Gray.copy(alpha = 0.5f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .background(brush, shape = RoundedCornerShape(8.dp))
    )
}


