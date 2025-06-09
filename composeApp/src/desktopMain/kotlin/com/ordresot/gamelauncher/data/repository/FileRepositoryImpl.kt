package com.ordresot.gamelauncher.data.repository

import com.ordresot.gamelauncher.domain.api.repository.FileRepository
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.filechooser.FileSystemView

class FileRepositoryImpl : FileRepository {
    override fun pickDirectory(): String? {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        chooser.dialogTitle = "Select Folder"
        return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else {
            null
        }
    }

    override fun pickFile(extension: String?, allowAll: Boolean, initialDirectory: File?): String? {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        initialDirectory?.let { chooser.currentDirectory = it }
        extension?.let {
            chooser.fileFilter = object : javax.swing.filechooser.FileFilter() {
                override fun accept(f: File) = f.isDirectory || f.name.endsWith(extension, ignoreCase = true)
                override fun getDescription() = "*$extension"
            }
        }
        if (!allowAll && extension != null) {
            chooser.setAcceptAllFileFilterUsed(false)
        }
        chooser.dialogTitle = "Select File"
        return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else {
            null
        }
    }
}