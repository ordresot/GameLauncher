package com.ordresot.gamelauncher.domain.api.usecase

import com.ordresot.gamelauncher.domain.model.Config

interface LoadConfigUseCase {
    operator fun invoke(): Config
}