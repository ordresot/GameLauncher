package com.ordresot.gamelauncher.domain.api.usecase

interface PickFileUseCase {
    operator fun invoke(vararg extensions: String, allowAll: Boolean = false): String?
}