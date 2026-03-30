package org.example.project.features.auth.data.datasources.local.tokens

import kotlinx.datetime.Instant
import org.example.project.features.auth.domain.repository.TokenRepositoryError
import org.example.project.utils.models.Outcome
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TokenLocalDataSource : TokenDataSource {
    override fun create(
        userId: Int,
        tokenValue: String,
        expiresAt: Instant,
    ): Outcome<RefreshTokenEntity, TokenRepositoryError> = try {
        transaction {
            val id = RefreshTokensTable.insertAndGetId {
                it[RefreshTokensTable.userId] = userId
                it[RefreshTokensTable.tokenValue] = tokenValue
                it[RefreshTokensTable.expiresAt] = expiresAt
            }
            Outcome.Success(
                RefreshTokenEntity(
                    id.value,
                    userId,
                    tokenValue,
                    expiresAt,
                ),
            )
        }
    } catch (e: ExposedSQLException) {
        if (e.sqlState == SQL_STATE_FOREIGN_KEY_VIOLATION) {
            Outcome.Error(
                TokenRepositoryError.USER_NOT_FOUND,
                "User with ID $userId not found",
            )
        } else {
            Outcome.Error(TokenRepositoryError.DATABASE_ERROR, e.message)
        }
    } catch (e: Exception) {
        Outcome.Error(TokenRepositoryError.DATABASE_ERROR, e.message)
    }

    override fun findByTokenValue(
        tokenValue: String,
    ): Outcome<RefreshTokenEntity, TokenRepositoryError> = try {
        transaction {
            val entity = RefreshTokensTable.selectAll()
                .where { RefreshTokensTable.tokenValue eq tokenValue }
                .singleOrNull()
                ?.let {
                    RefreshTokenEntity(
                        it[RefreshTokensTable.id].value,
                        it[RefreshTokensTable.userId].value,
                        it[RefreshTokensTable.tokenValue],
                        it[RefreshTokensTable.expiresAt],
                    )
                }
            if (entity != null) {
                Outcome.Success(entity)
            } else {
                Outcome.Error(TokenRepositoryError.TOKEN_NOT_FOUND)
            }
        }
    } catch (e: Exception) {
        Outcome.Error(TokenRepositoryError.DATABASE_ERROR, e.message)
    }

    override fun delete(
        tokenValue: String,
    ): Outcome<Unit, TokenRepositoryError> = try {
        transaction {
            val deletedCount = RefreshTokensTable
                .deleteWhere { RefreshTokensTable.tokenValue eq tokenValue }
            if (deletedCount > 0) {
                Outcome.Success(Unit)
            } else {
                Outcome.Error(TokenRepositoryError.TOKEN_NOT_FOUND)
            }
        }
    } catch (e: Exception) {
        Outcome.Error(TokenRepositoryError.DATABASE_ERROR, e.message)
    }

    override fun consume(
        tokenValue: String,
    ): Outcome<RefreshTokenEntity, TokenRepositoryError> = try {
        transaction {
            val entity = RefreshTokensTable.selectAll()
                .where { RefreshTokensTable.tokenValue eq tokenValue }
                .singleOrNull()
                ?.let {
                    RefreshTokenEntity(
                        it[RefreshTokensTable.id].value,
                        it[RefreshTokensTable.userId].value,
                        it[RefreshTokensTable.tokenValue],
                        it[RefreshTokensTable.expiresAt],
                    )
                }

            if (entity != null) {
                RefreshTokensTable.deleteWhere { RefreshTokensTable.tokenValue eq tokenValue }
                Outcome.Success(entity)
            } else {
                Outcome.Error(TokenRepositoryError.TOKEN_NOT_FOUND)
            }
        }
    } catch (e: Exception) {
        Outcome.Error(TokenRepositoryError.DATABASE_ERROR, e.message)
    }

    companion object {
        private const val SQL_STATE_FOREIGN_KEY_VIOLATION = "23506"
    }
}
