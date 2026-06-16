package org.example.project.presentation.auth

import org.example.project.domain.feature.auth.repositories.AuthLoginError

sealed interface AuthScreenState {
    object LogIn : AuthScreenState
    object Loading : AuthScreenState
    object Authorized : AuthScreenState
    data class Error(val error: AuthLoginError) : AuthScreenState
}
