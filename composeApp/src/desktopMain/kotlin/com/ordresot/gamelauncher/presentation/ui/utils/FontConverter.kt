package com.ordresot.gamelauncher.presentation.ui.utils

import androidx.compose.ui.text.font.FontWeight
import java.awt.Font as AwtFont

fun androidx.compose.ui.text.TextStyle.toAwtFont(): AwtFont {
    val weight = when (this.fontWeight) {
        FontWeight.Bold -> AwtFont.BOLD
        FontWeight.Normal -> AwtFont.PLAIN
        else -> AwtFont.PLAIN
    }
    val size = this.fontSize.value.toInt() // fontSize в sp, но на десктопе это хорошо ложится
    return AwtFont("Orbitron", weight, size) // Подставляем название твоего шрифта
}