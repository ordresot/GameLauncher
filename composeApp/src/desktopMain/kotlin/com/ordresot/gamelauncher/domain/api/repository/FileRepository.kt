package com.ordresot.gamelauncher.domain.api.repository

interface FileRepository {
    fun chooseDirectory(): String?
    fun chooseFile(vararg extensions: String, allowAll: Boolean = false): String?
}