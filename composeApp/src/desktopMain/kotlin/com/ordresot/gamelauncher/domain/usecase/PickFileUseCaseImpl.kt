package com.ordresot.gamelauncher.domain.usecase

import com.ordresot.gamelauncher.domain.api.repository.FileRepository
import com.ordresot.gamelauncher.domain.api.usecase.PickFileUseCase

class PickFileUseCaseImpl(
    private val repository: FileRepository
) : PickFileUseCase {
    override fun invoke(vararg extensions: String, allowAll: Boolean): String? {
        return repository.chooseFile(extensions = extensions, allowAll = allowAll)
    }
}