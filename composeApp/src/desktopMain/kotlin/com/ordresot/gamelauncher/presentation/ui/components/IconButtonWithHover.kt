package com.ordresot.gamelauncher.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IconButtonWithHover(
    onClick: () -> Unit,
    icon: String,
    description: String
) {
    var isHovered by remember { mutableStateOf(false) }
    val tint by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colors.secondary else MaterialTheme.colors.onPrimary,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .padding(8.dp) // <- добавили отступ вокруг
            .size(40.dp) // <- задаем удобный размер кнопки (как IconButton 40-48 dp обычно)
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(32.dp) // сам размер иконки
        )
    }
}