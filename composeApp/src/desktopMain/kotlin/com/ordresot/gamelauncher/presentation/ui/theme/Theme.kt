package com.ordresot.gamelauncher.presentation.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

// 🎨 Цветовая схема
val DarkColorPalette = darkColors(
    primary = Color(0xFFFF00FF),
    primaryVariant = Color(0xFF8B00FF),
    secondary = Color(0xFF00FFFF),
    background = Color(0xFF0D0D0D),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFF383838),
    onSurface = Color.White
)

// 🖋️ Типографика
val CustomTypography = Typography(
    h6 = TextStyle(
        fontFamily = FontFamily(Font(resource = "fonts/orbitron.ttf")),
        fontSize = 20.sp
    ),
    body2 = TextStyle(
        fontFamily = FontFamily(Font(resource = "fonts/audiowide.ttf")),
        fontSize = 14.sp
    )
)

// 🚀 Кастомная тема
@Composable
fun GameLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = CustomTypography,
        content = content
    )
}