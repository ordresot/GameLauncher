package com.ordresot.gamelauncher.domain.usecase

import com.ordresot.gamelauncher.domain.api.repository.GameRepository
import com.ordresot.gamelauncher.domain.api.usecase.LoadConfigUseCase
import com.ordresot.gamelauncher.domain.model.Config

class LoadConfigUseCaseImpl(
    private val repository: GameRepository
) : LoadConfigUseCase {
    override fun invoke(): Config {
        return repository.loadConfig()
    }
}