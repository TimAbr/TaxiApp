package org.example.project.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    object Auth : Screen

    @Serializable
    object Main : Screen

    @Serializable
    object LocationPermission : Screen
}
