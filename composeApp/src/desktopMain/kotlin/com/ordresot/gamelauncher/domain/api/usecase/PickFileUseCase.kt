package com.ordresot.gamelauncher.domain.api.usecase

import java.io.File

fun interface PickFileUseCase {
    operator fun invoke(
        extension: String?,
        allowAll: Boolean,
        initialDirectory: File?
    ): String?
}