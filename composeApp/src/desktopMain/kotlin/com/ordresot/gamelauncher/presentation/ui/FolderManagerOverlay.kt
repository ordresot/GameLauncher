package com.ordresot.gamelauncher.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ordresot.gamelauncher.presentation.ui.components.HoverButton
import com.ordresot.gamelauncher.presentation.ui.components.IconButtonWithHover

@Composable
fun FolderManagerOverlay(
    folders: List<String>,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onClose: () -> Unit,
    isFullscreen: Boolean
) {
    Box(Modifier.fillMaxSize()
        .background(MaterialTheme.colors.background.copy(alpha = 0.0f))
    )
    {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose() }
        )

        Surface(
            elevation = 10.dp,
            color = MaterialTheme.colors.surface,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary),
            modifier = Modifier.align(Alignment.Center).padding(horizontal = if(isFullscreen) 300.dp else 30.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Manage Folders", style = MaterialTheme.typography.h6)
                Spacer(Modifier.height(16.dp))

                folders.forEach { folder ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(folder, modifier = Modifier.weight(1f), color = MaterialTheme.colors.onSurface)
                        IconButtonWithHover(
                            onClick = { onRemoveFolder(folder) },
                            icon = "close.svg",
                            description = "Remove"
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                HoverButton(onClick = onAddFolder, text = "Add Folder")
            }
        }
    }
}