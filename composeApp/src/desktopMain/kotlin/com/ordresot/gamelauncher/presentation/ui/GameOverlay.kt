package com.ordresot.gamelauncher.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ordresot.gamelauncher.domain.model.GameEntry
import com.ordresot.gamelauncher.presentation.ui.components.HoverButton
import kotlinx.coroutines.delay
import java.awt.BorderLayout
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.io.File
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

@Composable
fun GameOverlay(
    game: GameEntry,
    onLaunch: () -> Unit,
    onSelectExe: () -> Unit,
    onSelectCover: () -> Unit,
    onClose: () -> Unit,
    isDragging: Boolean,
    isDropEnabled: Boolean,
    onDragStateChange: (Boolean) -> Unit,
    onCoverDropped: (String) -> Unit,
    isFullscreen: Boolean,
    overlayVisible: Boolean
) {
    var showDragPanel by remember { mutableStateOf(false) }

    LaunchedEffect(overlayVisible) {
        if (overlayVisible) {
            delay(500)
            showDragPanel = true
        } else {
            showDragPanel = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
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
            modifier = Modifier.align(Alignment.Center).padding(horizontal = if (isFullscreen) 300.dp else 30.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = game.folder.name,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    HoverButton(
                        onClick = onLaunch,
                        enabled = game.exePath != null,
                        text = "Launch"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colors.onSurface)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Executable: ${game.exePath?.let { File(it).name } ?: "Not selected"}",
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                HoverButton(
                    onClick = onSelectExe,
                    text = "Select .exe"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cover image: ${game.coverPath?.let { File(it).name } ?: "No cover selected"}",
                    color = MaterialTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier
                        .height(160.dp)
                        .width(300.dp)
                        .align(Alignment.Start)
                        .border(2.dp, if (isDragging) MaterialTheme.colors.primary else Color.Gray)
                        .padding(8.dp),
                    color = MaterialTheme.colors.surface
                ) {
                    if (showDragPanel) {
                        SwingPanel(
                            modifier = Modifier.fillMaxSize(),
                            factory = {
                                JPanel(BorderLayout()).apply {
                                    val label = JLabel("Drop cover image here", SwingConstants.CENTER)
                                    add(label)
                                    dropTarget = object : DropTarget() {
                                        override fun dragEnter(evt: DropTargetDragEvent) {
                                            onDragStateChange(true)
                                            label.text = ""
                                        }

                                        override fun dragExit(evt: DropTargetEvent) {
                                            onDragStateChange(false)
                                            label.text = "Drop cover image here"
                                        }

                                        override fun drop(evt: DropTargetDropEvent) {
                                            try {
                                                evt.acceptDrop(DnDConstants.ACTION_COPY)
                                                val transferable = evt.transferable
                                                val data = transferable.getTransferData(DataFlavor.javaFileListFlavor)
                                                if (data is List<*>) {
                                                    val files = data.filterIsInstance<File>()
                                                    val file = files.firstOrNull()
                                                    if (file != null) {
                                                        onCoverDropped(file.absolutePath)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            } finally {
                                                onDragStateChange(false)
                                                label.text = "Drop cover image here"
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Preparing Drop Area...",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}