package org.example.project.features.auth.domain.mappers

import org.example.project.features.auth.domain.repository.TokenRepositoryError
import org.example.project.features.auth.domain.service.AuthError

fun TokenRepositoryError.toAuthError(): AuthError = when (this) {
    TokenRepositoryError.USER_NOT_FOUND -> AuthError.UserNotFound
    TokenRepositoryError.DATABASE_ERROR -> AuthError.DatabaseError
    TokenRepositoryError.TOKEN_NOT_FOUND -> AuthError.InvalidToken
    TokenRepositoryError.TOKEN_EXPIRED -> AuthError.InvalidToken
}
