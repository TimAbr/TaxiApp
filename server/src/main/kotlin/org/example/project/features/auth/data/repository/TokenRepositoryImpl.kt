package org.example.project.features.auth.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.example.project.features.auth.data.datasources.local.tokens.RefreshTokenEntity
import org.example.project.features.auth.data.datasources.local.tokens.TokenDataSource
import org.example.project.features.auth.domain.repository.TokenRepository
import org.example.project.features.auth.domain.repository.TokenRepositoryError
import org.example.project.utils.models.Outcome

class TokenRepositoryImpl(
    private val tokenDataSource: TokenDataSource,
) : TokenRepository {

    override suspend fun saveRefreshToken(
        userId: Int,
        token: String,
        expiresAt: Instant,
    ): Outcome<Unit, TokenRepositoryError> {
        return when (
            val result = tokenDataSource.create(
                userId,
                token,
                expiresAt,
            )
        ) {
            is Outcome.Success -> Outcome.Success(Unit)
            is Outcome.Error -> result
        }
    }

    override suspend fun validateAndGetUserId(
        refreshToken: String,
    ): Outcome<Int, TokenRepositoryError> {
        return when (val result = tokenDataSource.findByTokenValue(refreshToken)) {
            is Outcome.Error -> result
            is Outcome.Success -> result.value.getUserIdIfValid()
        }
    }

    override suspend fun revokeRefreshToken(
        refreshToken: String,
    ): Outcome<Unit, TokenRepositoryError> {
        return tokenDataSource.delete(refreshToken)
    }

    override suspend fun consumeRefreshToken(
        refreshToken: String,
    ): Outcome<Int, TokenRepositoryError> {
        return when (val result = tokenDataSource.consume(refreshToken)) {
            is Outcome.Error -> result
            is Outcome.Success -> result.value.getUserIdIfValid()
        }
    }

    private fun RefreshTokenEntity.getUserIdIfValid(): Outcome<Int, TokenRepositoryError> {
        val now = Clock.System.now()
        return if (expiresAt < now) {
            Outcome.Error(TokenRepositoryError.TOKEN_EXPIRED)
        } else {
            Outcome.Success(userId)
        }
    }
}
