package com.ordresot.gamelauncher.presentation.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ordresot.gamelauncher.domain.model.GameEntry

@Composable
fun GameCard(
    game: GameEntry,
    animatedOffset: Float,
    animatedStrokeWidth: Float,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 16.dp)
            .drawBehind {
                val gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF00FF),
                        Color(0xFFDA70D6),
                        Color(0xFF00FFFF)
                    ),
                    start = Offset(animatedOffset, 0f),
                    end = Offset(0f, size.height + animatedOffset),
                    tileMode = TileMode.Mirror
                )
                drawRoundRect(
                    brush = gradient,
                    size = size,
                    cornerRadius = CornerRadius(16f, 16f),
                    style = Stroke(width = animatedStrokeWidth)
                )
            }
            .clickable { onClick() },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Box(modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f),
                    Color.Black.copy(alpha = 0.35f)
                )
            )
        )) {
            val bitmap = game.coverBitmap

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = game.folder.name,
                        color = MaterialTheme.colors.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
            }
        }
    }
}