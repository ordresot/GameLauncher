package com.ordresot.gamelauncher.di

import com.ordresot.gamelauncher.data.repository.FileRepositoryImpl
import com.ordresot.gamelauncher.data.repository.GameRepositoryImpl
import com.ordresot.gamelauncher.domain.api.repository.FileRepository
import com.ordresot.gamelauncher.domain.api.repository.GameRepository
import com.ordresot.gamelauncher.domain.api.usecase.LoadConfigUseCase
import com.ordresot.gamelauncher.domain.api.usecase.PickDirectoryUseCase
import com.ordresot.gamelauncher.domain.api.usecase.PickFileUseCase
import com.ordresot.gamelauncher.domain.api.usecase.SaveConfigUseCase
import com.ordresot.gamelauncher.domain.api.usecase.ScanGamesUseCase
import com.ordresot.gamelauncher.domain.usecase.LoadConfigUseCaseImpl
import com.ordresot.gamelauncher.domain.usecase.PickDirectoryUseCaseImpl
import com.ordresot.gamelauncher.domain.usecase.PickFileUseCaseImpl
import com.ordresot.gamelauncher.domain.usecase.SaveConfigUseCaseImpl
import com.ordresot.gamelauncher.domain.usecase.ScanGamesUseCaseImpl
import com.ordresot.gamelauncher.presentation.viewmodel.GameLauncherViewModel
import java.io.File

object DependencyProvider {

    private val configFile = File(System.getProperty("user.home"), ".gamelauncher_config.json")

    private val gameRepository: GameRepository by lazy { GameRepositoryImpl(configFile) }
    private val fileRepository: FileRepository by lazy { FileRepositoryImpl() }

    val loadConfigUseCase: LoadConfigUseCase by lazy { LoadConfigUseCaseImpl(gameRepository) }
    val saveConfigUseCase: SaveConfigUseCase by lazy { SaveConfigUseCaseImpl(gameRepository) }
    val scanGamesUseCase: ScanGamesUseCase by lazy { ScanGamesUseCaseImpl(gameRepository) }
    val pickDirectoryUseCase: PickDirectoryUseCase by lazy { PickDirectoryUseCaseImpl(fileRepository) }
    val pickFileUseCase: PickFileUseCase by lazy { PickFileUseCaseImpl(fileRepository) }

    fun provideGameLauncherViewModel(): GameLauncherViewModel {
        return GameLauncherViewModel(
            loadConfigUseCase,
            saveConfigUseCase,
            scanGamesUseCase,
            pickDirectoryUseCase,
            pickFileUseCase
        )
    }
}