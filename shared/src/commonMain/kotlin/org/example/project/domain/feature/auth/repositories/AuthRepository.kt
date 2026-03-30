package org.example.project.domain.feature.auth.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.domain.feature.auth.models.AuthMethod
import org.example.project.utils.models.Outcome

interface AuthRepository {
    val isAuthorized: StateFlow<Boolean>
    suspend fun login(method: AuthMethod): Outcome<Unit, AuthLoginError>
    suspend fun logout(): Outcome<Unit, AuthLogoutError>
}

sealed interface AuthLoginError {
    data object NetworkError : AuthLoginError
    data object InvalidToken : AuthLoginError
    data object ServerError : AuthLoginError
    data object Cancelled : AuthLoginError
    data object Unknown : AuthLoginError
}

sealed interface AuthLogoutError {
    data object NetworkError : AuthLogoutError
    data object Unknown : AuthLogoutError
}