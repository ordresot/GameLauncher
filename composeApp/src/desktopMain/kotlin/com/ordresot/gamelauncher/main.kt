package com.ordresot.gamelauncher

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@Serializable
data class GameInfo(
    val exePath: String? = null,
    val coverPath: String? = null
)

@Serializable
data class Config(
    val gameFolders: List<String> = emptyList(),
    val gameData: Map<String, GameInfo> = emptyMap()
)

data class GameEntry(
    val folder: File,
    var exePath: String? = null,
    var coverPath: String? = null
)

private val configFile = File(System.getProperty("user.home"), ".gamelauncher_config.json")

private val LightGreenColors = lightColors(
    primary = Color(0xFF81C784),
    primaryVariant = Color(0xFF66BB6A),
    secondary = Color(0xFFA5D6A7),
    background = Color(0xFFF0FFF0),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Game Launcher") {
        MaterialTheme(colors = LightGreenColors) {
            var config by remember { mutableStateOf(loadConfig()) }
            var folders by remember { mutableStateOf(config.gameFolders) }
            val gameEntries = remember { mutableStateListOf<GameEntry>() }
            gameEntries.clear()
            gameEntries.addAll(scanGames(folders, config.gameData))

            var selectedGame by remember { mutableStateOf<GameEntry?>(null) }
            var showFolderManager by remember { mutableStateOf(false) }

            fun removeFolder(path: String) {
                folders = folders - path
                config = config.copy(
                    gameFolders = folders,
                    gameData = config.gameData.filterKeys { it.startsWith(path).not() }
                )
                gameEntries.clear()
                gameEntries.addAll(scanGames(folders, config.gameData))
                saveConfig(config)
            }

            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Game Launcher") },
                    actions = {
                        IconButton(onClick = {
                            val selectedFolder = chooseDirectory()
                            if (selectedFolder != null && selectedFolder !in folders) {
                                folders = folders + selectedFolder
                                config = config.copy(gameFolders = folders)
                                val scanned = scanGames(folders, config.gameData)
                                gameEntries.clear()
                                gameEntries.addAll(scanned)
                                saveConfig(config)
                            }
                        }) {
                            Icon(
                                painter = painterResource("add.svg"),
                                contentDescription = "Добавить папку"
                            )
                        }
                        IconButton(onClick = { showFolderManager = true }) {
                            Icon(
                                painter = painterResource("delete.svg"),
                                contentDescription = "Управление папками"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(gameEntries) { game ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clickable { selectedGame = game },
                            elevation = 4.dp,
                            backgroundColor = LightGreenColors.primary
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val bitmap = remember(game.coverPath) {
                                    game.coverPath?.let {
                                        loadImage(it)
                                    }
                                }
                                bitmap?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = game.folder.name,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                if (showFolderManager) {
                    Window(
                        onCloseRequest = { showFolderManager = false },
                        resizable = false,
                        state = rememberWindowState(width = 300.dp, height = Dp.Unspecified)
                    ) {
                        Surface(elevation = 8.dp) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Управление папками", style = MaterialTheme.typography.h6)
                                Spacer(modifier = Modifier.height(8.dp))
                                folders.forEach { path ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = path,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.body2
                                        )
                                        IconButton(onClick = { removeFolder(path) }) {
                                            Icon(
                                                painter = painterResource("delete.svg"),
                                                contentDescription = "Удалить папку"
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = { showFolderManager = false }, modifier = Modifier.align(Alignment.End)) {
                                    Text("Закрыть")
                                }
                            }
                        }
                    }
                }

                selectedGame?.let { game ->
                    Window(
                        onCloseRequest = { selectedGame = null },
                        title = game.folder.name,
                        resizable = false,
                        state = rememberWindowState(width = 600.dp, height = Dp.Unspecified)
                    ) {
                        MaterialTheme(colors = LightGreenColors) {
                            Surface(
                                modifier = Modifier.wrapContentHeight(),
                                elevation = 10.dp
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = game.folder.name,
                                            style = MaterialTheme.typography.h6
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Button(
                                            onClick = {
                                                game.exePath?.let { path ->
                                                    Runtime.getRuntime().exec(path, null, game.folder)
                                                }
                                            },
                                            enabled = game.exePath != null,
                                            modifier = Modifier.defaultMinSize(minWidth = 100.dp)
                                        ) {
                                            Text("Запустить")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text("Файл запуска: ${game.exePath?.let { File(it).name } ?: "не выбрано"}")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(onClick = {
                                        val exe = chooseFile("Выберите .exe", filterExt = ".exe")
                                        if (exe != null) {
                                            game.exePath = exe
                                            config = config.copy(
                                                gameData = config.gameData + (game.folder.absolutePath to GameInfo(
                                                    exePath = exe,
                                                    coverPath = game.coverPath
                                                ))
                                            )
                                            saveConfig(config)
                                        }
                                    }) {
                                        Text("Выбрать exe")
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Обложка: ${game.coverPath?.let { File(it).name } ?: "не выбрана"}")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(onClick = {
                                        val img = chooseFile("Выберите обложку", filterExt = ".png", allowAll = true)
                                        if (img != null) {
                                            game.coverPath = img
                                            config = config.copy(
                                                gameData = config.gameData + (game.folder.absolutePath to GameInfo(
                                                    exePath = game.exePath,
                                                    coverPath = img
                                                ))
                                            )
                                            saveConfig(config)
                                        }
                                    }) {
                                        Text("Установить обложку")
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(onClick = { selectedGame = null }) {
                                            Text("Закрыть")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun chooseDirectory(): String? {
    val chooser = JFileChooser(FileSystemView.getFileSystemView())
    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    chooser.dialogTitle = "Выберите папку с играми"
    return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        chooser.selectedFile.absolutePath else null
}

fun chooseFile(title: String, filterExt: String? = null, allowAll: Boolean = false): String? {
    val chooser = JFileChooser(FileSystemView.getFileSystemView())
    chooser.dialogTitle = title
    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
    if (!allowAll && filterExt != null) {
        chooser.fileFilter = object : javax.swing.filechooser.FileFilter() {
            override fun accept(f: File) = f.isDirectory || f.name.endsWith(filterExt, ignoreCase = true)
            override fun getDescription() = "*$filterExt"
        }
    }
    return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        chooser.selectedFile.absolutePath else null
}

fun loadImage(path: String): ImageBitmap? = runCatching {
    val img: BufferedImage = ImageIO.read(File(path))
    img as ImageBitmap
}.getOrNull()

fun saveConfig(config: Config) = runCatching {
    configFile.writeText(Json.encodeToString(config))
}

fun loadConfig(): Config = runCatching {
    if (configFile.exists()) Json.decodeFromString(configFile.readText()) else Config()
}.getOrDefault(Config())

fun scanGames(folders: List<String>, gameData: Map<String, GameInfo>): List<GameEntry> =
    folders.flatMap { folder ->
        File(folder).listFiles()?.filter { it.isDirectory }?.map { dir ->
            val info = gameData[dir.absolutePath]
            GameEntry(
                folder = dir,
                exePath = info?.exePath,
                coverPath = info?.coverPath
            )
        } ?: emptyList()
    }