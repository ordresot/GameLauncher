package com.ordresot.gamelauncher.domain.usecase

import com.ordresot.gamelauncher.domain.api.repository.GameRepository
import com.ordresot.gamelauncher.domain.api.usecase.SaveConfigUseCase
import com.ordresot.gamelauncher.domain.model.Config

class SaveConfigUseCaseImpl(
    private val repository: GameRepository
) : SaveConfigUseCase {
    override fun invoke(config: Config) {
        repository.saveConfig(config)
    }
}