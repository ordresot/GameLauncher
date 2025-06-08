package com.ordresot.gamelauncher.domain.usecase

import com.ordresot.gamelauncher.domain.api.repository.GameRepository
import com.ordresot.gamelauncher.domain.api.usecase.ScanGamesUseCase
import com.ordresot.gamelauncher.domain.model.GameEntry
import com.ordresot.gamelauncher.domain.model.GameInfo

class ScanGamesUseCaseImpl(
    private val repository: GameRepository
) : ScanGamesUseCase {
    override fun invoke(folders: List<String>, gameData: Map<String, GameInfo>): List<GameEntry> {
        return repository.scanGames(folders, gameData)
    }
}