import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.api.tasks.Copy

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "1.9.22"
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.ordresot.gamelauncher.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            windows {
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
            }
            packageName = "Game Launcher"
            packageVersion = "2.0.0"
        }
    }
}

val exportInstaller by tasks.registering(Copy::class) {
    dependsOn("packageMsi")

    from(file("build/compose/binaries/main/msi")) {
        include("*.msi")
    }

    into(rootProject.layout.projectDirectory.dir("release"))
}