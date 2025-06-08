package com.ordresot.gamelauncher.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GameInfo(
    val exePath: String? = null,
    val coverPath: String? = null
)