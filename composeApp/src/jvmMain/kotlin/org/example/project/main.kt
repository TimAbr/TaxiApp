package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.di.appModule
import org.example.project.di.initKoin

fun main() {
    initKoin {
        modules(appModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "TaxiApp",
        ) {
            App()
        }
    }
}
