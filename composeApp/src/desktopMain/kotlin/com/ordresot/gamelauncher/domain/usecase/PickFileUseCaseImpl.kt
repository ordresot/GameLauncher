package com.ordresot.gamelauncher.domain.usecase

import com.ordresot.gamelauncher.domain.api.repository.FileRepository
import com.ordresot.gamelauncher.domain.api.usecase.PickFileUseCase
import java.io.File
import javax.swing.JFileChooser

class PickFileUseCaseImpl(
    private val repository: FileRepository
) : PickFileUseCase {
    override fun invoke(
        extension: String?,
        allowAll: Boolean,
        initialDirectory: File?
    ): String? {
        return repository.pickFile(extension, allowAll, initialDirectory)
    }
}