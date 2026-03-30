package org.example.project.features.auth.domain.repository

import kotlinx.datetime.Instant
import org.example.project.utils.models.Outcome

enum class TokenRepositoryError {
    DATABASE_ERROR,
    TOKEN_NOT_FOUND,
    TOKEN_EXPIRED,
    USER_NOT_FOUND,
}

interface TokenRepository {
    suspend fun saveRefreshToken(
        userId: Int,
        token: String,
        expiresAt: Instant,
    ): Outcome<Unit, TokenRepositoryError>

    suspend fun validateAndGetUserId(
        refreshToken: String,
    ): Outcome<Int, TokenRepositoryError>

    suspend fun revokeRefreshToken(
        refreshToken: String,
    ): Outcome<Unit, TokenRepositoryError>
}
