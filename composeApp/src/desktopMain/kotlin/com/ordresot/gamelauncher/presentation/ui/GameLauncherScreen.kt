package com.ordresot.gamelauncher.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ordresot.gamelauncher.presentation.ui.components.IconButtonWithHover
import com.ordresot.gamelauncher.presentation.viewmodel.GameLauncherViewModel

@Composable
fun GameLauncherScreen(viewModel: GameLauncherViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedStrokeWidth by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
        val scaffoldState = rememberScaffoldState()

        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { message ->
                scaffoldState.snackbarHostState.showSnackbar(message)
                viewModel.dismissError()
            }
        }

        Scaffold(
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colors.background,
            topBar = { /* твой TopAppBar */ },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text("Game Launcher", color = MaterialTheme.colors.onPrimary) },
                        backgroundColor = MaterialTheme.colors.primary,
                        actions = {
                            IconButtonWithHover(onClick = { viewModel.showFolderManager() }, icon = "folders_manager.svg", description = "Manage folders")
                            IconButtonWithHover(
                                onClick = { viewModel.toggleFullscreen() },
                                icon = if (uiState.isFullscreen) "fullscreen_exit.svg" else "fullscreen.svg",
                                description = "Toggle Fullscreen"
                            )
                            IconButtonWithHover(onClick = { viewModel.exit() }, icon = "close.svg", description = "Exit")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(300.dp),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        items(uiState.games) { game ->
                            GameCard(
                                game = game,
                                animatedOffset = animatedOffset,
                                animatedStrokeWidth = animatedStrokeWidth,
                                onClick = { viewModel.selectGame(game) }
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = uiState.showFolderManager) {
                    FolderManagerOverlay(
                        folders = uiState.folders,
                        onAddFolder = { viewModel.addFolder() },
                        onRemoveFolder = { path -> viewModel.removeFolder(path) },
                        onClose = { viewModel.closeFolderManager() },
                        isFullscreen = viewModel.isFullscreen()
                    )
                }

                AnimatedVisibility(visible = uiState.overlayVisible && uiState.selectedGame != null,) {
                    uiState.selectedGame?.let { game ->
                        GameOverlay(
                            game = game,
                            onLaunch = { viewModel.launchGame() },
                            onSelectExe = { viewModel.selectExe() },
                            onClose = { viewModel.deselectGame() },
                            isDragging = uiState.isDragging,
                            isDropEnabled = uiState.isDropEnabled,
                            onDragStateChange = { viewModel.setDragging(it) },
                            onCoverDropped = { path -> viewModel.updateCoverFromDrop(path) },
                            isFullscreen = uiState.isFullscreen,
                            overlayVisible = uiState.overlayVisible
                        )
                    }
                }
            }
        }
    }
}