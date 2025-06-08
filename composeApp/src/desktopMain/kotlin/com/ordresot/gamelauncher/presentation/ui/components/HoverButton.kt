package com.ordresot.gamelauncher.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HoverButton(onClick: () -> Unit, enabled: Boolean = true, text: String) {
    var isHovered by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colors.secondary else MaterialTheme.colors.primary,
        animationSpec = tween(300)
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = Modifier
            .defaultMinSize(minWidth = 100.dp)
            .pointerMoveFilter(
                onEnter = {
                    isHovered = true
                    false
                },
                onExit = {
                    isHovered = false
                    false
                }
            )
    ) {
        Text(text, color = Color.Black)
    }
}