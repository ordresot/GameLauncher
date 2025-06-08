package com.ordresot.gamelauncher

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ordresot.gamelauncher.di.DependencyProvider
import com.ordresot.gamelauncher.presentation.ui.GameLauncherScreen
import com.ordresot.gamelauncher.presentation.ui.theme.GameLauncherTheme

fun main() = application {
    val windowState = rememberWindowState()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Game Launcher",
        state = rememberWindowState(),
        undecorated = true  // чтобы без заголовков
    ) {
        val viewModel = remember { DependencyProvider.provideGameLauncherViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        // сразу после открытия окна
        LaunchedEffect(Unit) {
            window.extendedState = java.awt.Frame.MAXIMIZED_BOTH
        }

        LaunchedEffect(uiState.isFullscreen) {
            window.extendedState = if (uiState.isFullscreen) {
                java.awt.Frame.MAXIMIZED_BOTH
            } else {
                java.awt.Frame.NORMAL
            }
        }

        GameLauncherTheme {
            GameLauncherScreen(viewModel)
        }
    }
}