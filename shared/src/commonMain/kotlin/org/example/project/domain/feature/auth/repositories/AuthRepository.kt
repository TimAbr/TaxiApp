package org.example.project.domain.feature.auth.repositories

import kotlinx.coroutines.flow.StateFlow
import org.example.project.domain.feature.auth.models.AuthMethod
import org.example.project.utils.models.Outcome

interface AuthRepository {
    val isAuthorized: StateFlow<Boolean>
    suspend fun login(method: AuthMethod): Outcome<Unit, AuthLoginError>
    suspend fun logout(): Outcome<Unit, AuthLogoutError>
}

sealed interface AuthLoginError {
    object NetworkError : AuthLoginError
    object ServerError : AuthLoginError
    object Unknown : AuthLoginError
    
    interface GoogleAuthError : AuthLoginError {
        object Cancelled : GoogleAuthError
        object InvalidToken : AuthLoginError
    }
}

sealed interface AuthLogoutError {
    object NetworkError : AuthLogoutError
    object Unknown : AuthLogoutError
}
