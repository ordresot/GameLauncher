package com.ordresot.gamelauncher.presentation.ui.utils

import androidx.compose.ui.graphics.Color
import java.awt.Color as AwtColor

fun Color.toAwtColor(): AwtColor {
    return AwtColor(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        (alpha * 255).toInt()
    )
}