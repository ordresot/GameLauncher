package com.ordresot.gamelauncher.presentation.ui.state

import com.ordresot.gamelauncher.domain.model.GameEntry

data class GameLauncherUiState(
    val games: List<GameEntry> = emptyList(),
    val folders: List<String> = emptyList(),
    val selectedGame: GameEntry? = null,
    val overlayVisible: Boolean = false,
    val showFolderManager: Boolean = false,
    val isDragging: Boolean = false,
    val isDropEnabled: Boolean = false,
    val isFullscreen: Boolean = true,
    val errorMessage: String? = null
)