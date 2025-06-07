package com.ordresot.gamelauncher

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.BorderLayout
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.filechooser.FileSystemView

const val ANIMATION_DELAY = 300L

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

private val colorTheme = darkColors(
    primary = Color(0xFFFF00FF),
    primaryVariant = Color(0xFF8B00FF),
    secondary = Color(0xFF00FFFF),
    background = Color(0xFF0D0D0D),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFF39FF14),
    onSurface = Color.White
)

private val customTypography = Typography(
    h6 = TextStyle(
        fontFamily = FontFamily(Font(resource = "fonts/orbitron.ttf")),
        fontSize = 20.sp
    ),
    body2 = TextStyle(
        fontFamily = FontFamily(Font(resource = "fonts/audiowide.ttf")),
        fontSize = 14.sp
    )
)

fun main() = application {
    val windowState = rememberWindowState()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Game Launcher",
        state = windowState,
        undecorated = true
    ) {
        var isFullscreen by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            window.extendedState = java.awt.Frame.MAXIMIZED_BOTH
        }

        MaterialTheme(colors = colorTheme, typography = customTypography) {
            var config by remember { mutableStateOf(loadConfig()) }
            var folders by remember { mutableStateOf(config.gameFolders) }
            val gameEntries = remember { mutableStateListOf<GameEntry>() }
            gameEntries.clear()
            gameEntries.addAll(scanGames(folders, config.gameData))

            var selectedGame by remember { mutableStateOf<GameEntry?>(null) }
            var overlayVisible by remember { mutableStateOf(false) }
            var showFolderManager by remember { mutableStateOf(false) }
            var isDragging by remember { mutableStateOf(false) }
            var isDropEnabled by remember { mutableStateOf(false) }

            val infiniteTransition = rememberInfiniteTransition()

            val animatedOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val animatedStrokeWidth by infiniteTransition.animateFloat(
                initialValue = 6f,
                targetValue = 12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text("Game Launcher", color = MaterialTheme.colors.onPrimary) },
                        backgroundColor = MaterialTheme.colors.primary,
                        actions = {
                            IconButtonWithHover(onClick = { showFolderManager = true }, icon = "delete.svg", description = "Manage folders")
                            IconButtonWithHover(onClick = { exitApplication() }, icon = "close.svg", description = "Exit")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(220.dp),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        items(gameEntries) { game ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .padding(vertical = 16.dp)
                                    .drawBehind {
                                        val gradient = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFFF00FF),
                                                Color(0xFFDA70D6),
                                                Color(0xFF00FFFF)
                                            ),
                                            start = Offset(animatedOffset, 0f),
                                            end = Offset(0f, size.height + animatedOffset),
                                            tileMode = TileMode.Mirror
                                        )
                                        drawRoundRect(
                                            brush = gradient,
                                            size = size,
                                            cornerRadius = CornerRadius(16f, 16f),
                                            style = Stroke(width = animatedStrokeWidth)
                                        )
                                    }
                                    .clickable {
                                        selectedGame = game
                                        overlayVisible = true
                                        isDropEnabled = true
                                    },
                                backgroundColor = MaterialTheme.colors.surface,
                                elevation = 4.dp
                            ) {
                                Box(modifier = Modifier.background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.15f),
                                            Color.Black.copy(alpha = 0.35f)
                                        )
                                    )
                                )) {
                                    val bitmap = game.coverPath?.let { loadImage(it) }

                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = game.folder.name,
                                                color = MaterialTheme.colors.onSurface,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.fillMaxWidth().padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = showFolderManager) {
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background.copy(alpha = 0.8f))) {
                        Surface(
                            elevation = 10.dp,
                            color = MaterialTheme.colors.surface,
                            border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                            modifier = Modifier.align(Alignment.Center).padding(24.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Manage Folders", style = MaterialTheme.typography.h6)
                                Spacer(Modifier.height(16.dp))

                                folders.forEach { folder ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(folder, modifier = Modifier.weight(1f), color = MaterialTheme.colors.onSurface)
                                        IconButton(onClick = {
                                            folders = folders - folder
                                        }) {
                                            Icon(
                                                painterResource("close.svg"),
                                                contentDescription = "Remove",
                                                tint = MaterialTheme.colors.onPrimary
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))
                                HoverButton(onClick = {
                                    val selectedFolder = chooseDirectory("Select folder to add")
                                    if (selectedFolder != null && selectedFolder !in folders) {
                                        folders = folders + selectedFolder
                                    }
                                }, text = "Add Folder")

                                Spacer(Modifier.height(16.dp))
                                HoverButton(onClick = { showFolderManager = false }, text = "Close")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IconButtonWithHover(onClick: () -> Unit, icon: String, description: String) {
    var isHovered by remember { mutableStateOf(false) }
    val tint by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colors.secondary else MaterialTheme.colors.onPrimary,
        animationSpec = tween(durationMillis = 300)
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier.pointerMoveFilter(
            onEnter = {
                isHovered = true
                false
            },
            onExit = {
                isHovered = false
                false
            }
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = tint
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HoverButton(onClick: () -> Unit, enabled: Boolean = true, text: String) {
    var isHovered by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colors.secondary else MaterialTheme.colors.primary,
        animationSpec = tween(300)
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = Modifier
            .defaultMinSize(minWidth = 100.dp)
            .pointerMoveFilter(
                onEnter = {
                    isHovered = true
                    false
                },
                onExit = {
                    isHovered = false
                    false
                }
            )
    ) {
        Text(text, color = Color.Black)
    }
}

fun chooseDirectory(title: String = "Select folder"): String? {
    val chooser = JFileChooser(FileSystemView.getFileSystemView())
    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    chooser.dialogTitle = title
    return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        chooser.selectedFile.absolutePath else null
}

fun chooseFile(
    title: String,
    initialDirectory: File? = null,
    filterExt: String? = null,
    allowAll: Boolean = false
): String? {
    val chooser = JFileChooser(FileSystemView.getFileSystemView())
    chooser.dialogTitle = title
    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
    initialDirectory?.let {
        chooser.currentDirectory = it
    }
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
    val file = File(path)
    if (file.exists() && file.isFile) {
        val img: BufferedImage = ImageIO.read(file)
        img.toComposeImageBitmap()
    } else {
        null
    }
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