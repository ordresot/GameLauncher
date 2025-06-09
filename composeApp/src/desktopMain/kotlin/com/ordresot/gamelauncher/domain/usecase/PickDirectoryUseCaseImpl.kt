package com.ordresot.gamelauncher.domain.usecase

import com.ordresot.gamelauncher.domain.api.repository.FileRepository
import com.ordresot.gamelauncher.domain.api.usecase.PickDirectoryUseCase

class PickDirectoryUseCaseImpl(
    private val repository: FileRepository
) : PickDirectoryUseCase {
    override fun invoke(): String? {
        return repository.pickDirectory()
    }
}