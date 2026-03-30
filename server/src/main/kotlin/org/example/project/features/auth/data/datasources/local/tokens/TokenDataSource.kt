package org.example.project.features.auth.data.datasources.local.tokens

import kotlinx.datetime.Instant
import org.example.project.features.auth.domain.repository.TokenRepositoryError
import org.example.project.utils.models.Outcome

interface TokenDataSource {
    fun create(
        userId: Int,
        tokenValue: String,
        expiresAt: Instant,
    ): Outcome<RefreshTokenEntity, TokenRepositoryError>

    fun findByTokenValue(
        tokenValue: String,
    ): Outcome<RefreshTokenEntity, TokenRepositoryError>

    fun delete(
        tokenValue: String,
    ): Outcome<Unit, TokenRepositoryError>
}
