package com.ordresot.gamelauncher.domain.api.usecase

import com.ordresot.gamelauncher.domain.model.GameEntry
import com.ordresot.gamelauncher.domain.model.GameInfo

interface ScanGamesUseCase {
    operator fun invoke(folders: List<String>, gameData: Map<String, GameInfo>): List<GameEntry>
}