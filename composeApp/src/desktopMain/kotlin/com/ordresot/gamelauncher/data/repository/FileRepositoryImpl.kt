package com.ordresot.gamelauncher.data.repository

import com.ordresot.gamelauncher.domain.api.repository.FileRepository
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.filechooser.FileSystemView

class FileRepositoryImpl : FileRepository {

    override fun chooseDirectory(): String? {
        val chooser = JFileChooser(FileSystemView.getFileSystemView())
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        chooser.dialogTitle = "Select folder"
        return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else {
            null
        }
    }

    override fun chooseFile(vararg extensions: String, allowAll: Boolean): String? {
        val chooser = JFileChooser(FileSystemView.getFileSystemView())
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        chooser.dialogTitle = "Select file"
        if (!allowAll && extensions.isNotEmpty()) {
            chooser.fileFilter = FileNameExtensionFilter(
                extensions.joinToString(", ") { "*.$it" },
                *extensions
            )
        }
        return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else {
            null
        }
    }
}