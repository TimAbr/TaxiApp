package org.example.project.presentation.main

sealed interface MainEvent {
    object LogoutSuccess : MainEvent
}
