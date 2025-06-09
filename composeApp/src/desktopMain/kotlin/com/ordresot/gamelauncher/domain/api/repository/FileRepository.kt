package com.ordresot.gamelauncher.domain.api.repository

import java.io.File

interface FileRepository {
    fun pickDirectory(): String?
    fun pickFile(extension: String?, allowAll: Boolean, initialDirectory: File? = null): String?
}