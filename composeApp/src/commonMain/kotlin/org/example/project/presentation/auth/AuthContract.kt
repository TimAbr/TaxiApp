package org.example.project.presentation.auth

import org.example.project.domain.feature.auth.repositories.AuthLoginError

data class AuthState(
    val isLoading: Boolean = false,
    val error: AuthLoginError? = null,
    val isAuthorized: Boolean = false
)

