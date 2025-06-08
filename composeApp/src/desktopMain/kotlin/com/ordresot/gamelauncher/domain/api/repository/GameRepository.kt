package com.ordresot.gamelauncher.domain.api.repository

import com.ordresot.gamelauncher.domain.model.Config
import com.ordresot.gamelauncher.domain.model.GameEntry
import com.ordresot.gamelauncher.domain.model.GameInfo

interface GameRepository {
    fun loadConfig(): Config
    fun saveConfig(config: Config)
    fun scanGames(folders: List<String>, gameData: Map<String, GameInfo>): List<GameEntry>
}