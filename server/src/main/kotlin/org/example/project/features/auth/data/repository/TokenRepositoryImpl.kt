package org.example.project.features.auth.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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
        val result = tokenDataSource.findByTokenValue(refreshToken)
        if (result is Outcome.Error) return result

        val tokenEntity = (result as Outcome.Success).value
        val now = Clock.System.now()
        if (tokenEntity.expiresAt < now) {
            return Outcome.Error(TokenRepositoryError.TOKEN_EXPIRED)
        }

        return Outcome.Success(tokenEntity.userId)
    }

    override suspend fun revokeRefreshToken(
        refreshToken: String,
    ): Outcome<Unit, TokenRepositoryError> {
        return tokenDataSource.delete(refreshToken)
    }
}
