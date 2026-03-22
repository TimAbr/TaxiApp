package org.example.project.features.auth.domain.service

sealed class AuthError {
    object InvalidToken : AuthError()
    object UserNotFound : AuthError()
    object DatabaseError : AuthError()
    object ExternalServiceError : AuthError()
}