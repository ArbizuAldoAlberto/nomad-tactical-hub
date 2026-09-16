package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkTacticalColorScheme = darkColorScheme(
    primary = CyberGreen,
    secondary = BrightCyan,
    tertiary = WarnAmber,
    background = SlateCharcoal,
    surface = SurfaceDark,
    onPrimary = DarkText,
    onSecondary = DarkText,
    onTertiary = DarkText,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = SurfaceMedium,
    onSurfaceVariant = Color(0xFFECEFF1),
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme for tactical-screen design
    dynamicColor: Boolean = false, // Disable to preserve the custom cyberdeck aesthetics
    content: @Composable () -> Unit
) {
    // Always use our DarkTacticalColorScheme for custom bunker terminal look
    val colorScheme = DarkTacticalColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
