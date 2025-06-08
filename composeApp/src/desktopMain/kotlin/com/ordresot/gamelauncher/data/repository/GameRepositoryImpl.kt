package com.ordresot.gamelauncher.data.repository

import com.ordresot.gamelauncher.domain.api.repository.GameRepository
import com.ordresot.gamelauncher.domain.model.Config
import com.ordresot.gamelauncher.domain.model.GameEntry
import com.ordresot.gamelauncher.domain.model.GameInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class GameRepositoryImpl(
    private val configFile: File
) : GameRepository {

    override fun loadConfig(): Config {
        return runCatching {
            if (configFile.exists()) {
                Json.decodeFromString<Config>(configFile.readText())
            } else {
                Config()
            }
        }.getOrDefault(Config())
    }

    override fun saveConfig(config: Config) {
        runCatching {
            configFile.writeText(Json.encodeToString(config))
        }
    }

    override fun scanGames(folders: List<String>, gameData: Map<String, GameInfo>): List<GameEntry> {
        return folders.flatMap { folderPath ->
            File(folderPath).listFiles()
                ?.filter { it.isDirectory }
                ?.map { dir ->
                    val info = gameData[dir.absolutePath]
                    GameEntry(
                        folder = dir,
                        exePath = info?.exePath,
                        coverPath = info?.coverPath
                    )
                } ?: emptyList()
        }
    }
}
