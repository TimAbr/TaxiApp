package org.example.project.presentation.main

sealed interface MainSheetState {
    object Hidden : MainSheetState
    object LogoutConfirmation : MainSheetState
}
