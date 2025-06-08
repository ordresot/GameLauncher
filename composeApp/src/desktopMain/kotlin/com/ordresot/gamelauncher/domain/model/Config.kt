package com.ordresot.gamelauncher.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val gameFolders: List<String> = emptyList(),
    val gameData: Map<String, GameInfo> = emptyMap()
)