package com.ordresot.gamelauncher.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

data class GameEntry(
    val folder: File,
    var exePath: String? = null,
    var coverPath: String? = null
) {
    fun toGameInfo(): GameInfo {
        return GameInfo(
            exePath = this.exePath,
            coverPath = this.coverPath
        )
    }
    // Кэшируем bitmap лениво
    @Transient
    private var _coverBitmap: ImageBitmap? = null
    @Transient
    private var _lastLoadedPath: String? = null

    val coverBitmap: ImageBitmap?
        get() {
            if (coverPath != _lastLoadedPath) {
                _coverBitmap = coverPath?.let { path ->
                    runCatching {
                        val file = File(path)
                        if (file.exists() && file.isFile) {
                            val img: BufferedImage = ImageIO.read(file)
                            img.toComposeImageBitmap()
                        } else {
                            null
                        }
                    }.getOrNull()
                }
                _lastLoadedPath = coverPath
            }
            return _coverBitmap
        }
}