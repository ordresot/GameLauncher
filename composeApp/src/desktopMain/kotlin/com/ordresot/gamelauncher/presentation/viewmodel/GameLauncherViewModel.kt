package com.ordresot.gamelauncher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ordresot.gamelauncher.domain.api.usecase.*
import com.ordresot.gamelauncher.domain.model.GameEntry
import com.ordresot.gamelauncher.presentation.ui.state.GameLauncherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameLauncherViewModel(
    private val loadConfigUseCase: LoadConfigUseCase,
    private val saveConfigUseCase: SaveConfigUseCase,
    private val scanGamesUseCase: ScanGamesUseCase,
    private val pickDirectoryUseCase: PickDirectoryUseCase,
    private val pickFileUseCase: PickFileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameLauncherUiState(isFullscreen = true))
    val uiState = _uiState.asStateFlow()

    private var config = loadConfigUseCase()

    init {
        scanGames()
        _uiState.update { it.copy(folders = config.gameFolders) }
    }

    fun addFolder() {
        val folder = pickDirectoryUseCase()
        if (folder != null && folder !in _uiState.value.folders) {
            val updatedFolders = _uiState.value.folders + folder
            config = config.copy(gameFolders = updatedFolders)
            saveConfig()
            scanGames()
            _uiState.update { it.copy(folders = updatedFolders) }
        }
    }

    fun removeFolder(folder: String) {
        val updatedFolders = _uiState.value.folders - folder
        config = config.copy(
            gameFolders = updatedFolders,
            gameData = config.gameData.filterKeys { !it.startsWith(folder) }
        )
        saveConfig()
        scanGames()
        _uiState.update { it.copy(folders = updatedFolders) }
    }

    fun selectGame(game: GameEntry) {
        _uiState.update {
            it.copy(
                selectedGame = game,
                overlayVisible = true,
                isDropEnabled = true
            )
        }
    }

    fun deselectGame() {
        _uiState.update {
            it.copy(
                selectedGame = null,
                overlayVisible = false,
                isDropEnabled = false
            )
        }
    }

    fun showFolderManager() {
        _uiState.update { it.copy(showFolderManager = true) }
    }

    fun closeFolderManager() {
        _uiState.update { it.copy(showFolderManager = false) }
    }

    fun launchGame() {
        _uiState.value.selectedGame?.exePath?.let { exePath ->
            val folder = _uiState.value.selectedGame?.folder
            if (folder != null) {
                Runtime.getRuntime().exec(exePath, null, folder)
            }
        }
    }

    fun selectExe() {
        val initialDir = _uiState.value.selectedGame?.folder
        val exePath = pickFileUseCase(
            extension = "exe",
            allowAll = false,
            initialDirectory = initialDir
        )
        exePath?.let { path ->
            _uiState.value.selectedGame?.let { game ->
                val updatedGame = game.copy(exePath = path)
                updateGameInfo(updatedGame)
                _uiState.update { it.copy(selectedGame = updatedGame) }
            }
        }
    }

    fun setDragging(isDragging: Boolean) {
        _uiState.update { it.copy(isDragging = isDragging) }
    }

    fun toggleFullscreen() {
        _uiState.update { it.copy(isFullscreen = !_uiState.value.isFullscreen) }
    }

    fun exit() {
        kotlin.system.exitProcess(0)
    }

    private fun updateGameInfo(game: GameEntry) {
        config = config.copy(
            gameData = config.gameData + (game.folder.absolutePath to game.toGameInfo())
        )
        saveConfig()
        scanGames()
    }

    private fun saveConfig() {
        saveConfigUseCase(config)
    }

    private fun scanGames() {
        val games = scanGamesUseCase(config.gameFolders, config.gameData)
        _uiState.update { it.copy(games = games) }
    }

    fun updateCoverFromDrop(path: String) {
        if (path.endsWith(".png", ignoreCase = true) || path.endsWith(".jpg", ignoreCase = true)) {
            _uiState.value.selectedGame?.let { game ->
                val updatedGame = game.copy(coverPath = path)
                updateGameInfo(updatedGame)
                _uiState.update { it.copy(selectedGame = updatedGame) }
            }
        } else {
            _uiState.update { it.copy(errorMessage = "Only .png or .jpg files are allowed.") }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun isFullscreen(): Boolean{
        return _uiState.value.isFullscreen
    }
}