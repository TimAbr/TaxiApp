package org.example.project.features.auth.domain.service

import org.example.project.features.auth.domain.model.AuthTokenPair
import org.example.project.features.auth.domain.repository.UserRepository
import org.example.project.features.auth.domain.repository.TokenRepository
import org.example.project.features.auth.domain.repository.TokenRepositoryError
import org.example.project.features.auth.domain.external.ExternalAuthService
import org.example.project.features.auth.domain.external.ExternalAuthError
import org.example.project.utils.models.Outcome

class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val externalAuthService: ExternalAuthService,
    private val tokenManager: TokenManager
) {

    suspend fun authenticateWithGoogle(idTokenString: String): Outcome<AuthTokenPair, AuthError> {
        when (val googleResult = externalAuthService.verifyToken(idTokenString)) {
            is Outcome.Error -> {
                return when (googleResult.code) {
                    ExternalAuthError.INVALID_TOKEN ->
                        Outcome.Error(AuthError.InvalidToken, googleResult.message)
                    else ->
                        Outcome.Error(AuthError.ExternalServiceError, googleResult.message)
                }
            }
            is Outcome.Success -> {
                val googleUser = googleResult.value
                return when (
                    val userResult = userRepository.findOrCreateUser(
                        googleUser.email,
                        googleUser.name
                    )
                ) {
                    is Outcome.Success -> generateTokensForUser(userResult.value.id)
                    is Outcome.Error -> Outcome.Error(
                        AuthError.DatabaseError,
                        userResult.message
                    )
                }
            }
        }
    }

    suspend fun refreshTokens(
        refreshToken: String
    ): Outcome<AuthTokenPair, AuthError> {
        return when (
            val userIdResult = tokenRepository.validateAndGetUserId(refreshToken)
        ) {
            is Outcome.Error -> {
                Outcome.Error(
                    AuthError.InvalidToken,
                    "Invalid or expired refresh token"
                )
            }
            is Outcome.Success -> {
                val userId = userIdResult.value
                when (
                    val revokeResult = tokenRepository.revokeRefreshToken(refreshToken)
                ) {
                    is Outcome.Success -> generateTokensForUser(userId)
                    is Outcome.Error ->
                        Outcome.Error(
                            AuthError.DatabaseError,
                            revokeResult.message
                        )
                }
            }
        }
    }

    private suspend fun generateTokensForUser(userId: Int): Outcome<AuthTokenPair, AuthError> {
        val accessToken = tokenManager.generateAccessToken(userId)
        val refreshToken = tokenManager.generateRefreshToken()
        val refreshExpiresAt = tokenManager.getRefreshTokenExpiration()
        
        return when (
            val saveResult = tokenRepository.saveRefreshToken(
                userId,
                refreshToken,
                refreshExpiresAt
            )
        ) {
            is Outcome.Success -> Outcome.Success(AuthTokenPair(accessToken, refreshToken))
            is Outcome.Error -> {
                val errorType = if (saveResult.code == TokenRepositoryError.USER_NOT_FOUND) {
                    AuthError.UserNotFound
                } else {
                    AuthError.DatabaseError
                }
                Outcome.Error(errorType, saveResult.message)
            }
        }
    }
}
