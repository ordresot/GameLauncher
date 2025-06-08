package com.ordresot.gamelauncher.domain.api.usecase

import com.ordresot.gamelauncher.domain.model.Config

interface SaveConfigUseCase {
    operator fun invoke(config: Config)
}