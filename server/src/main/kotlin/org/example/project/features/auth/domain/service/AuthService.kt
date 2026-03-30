package org.example.project.features.auth.domain.service

import org.example.project.features.auth.domain.model.AuthTokenPair
import org.example.project.features.auth.domain.repository.UserRepository
import org.example.project.features.auth.domain.repository.TokenRepository
import org.example.project.features.auth.domain.external.ExternalAuthService
import org.example.project.features.auth.domain.mappers.toAuthError
import org.example.project.utils.models.Outcome

class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val externalAuthService: ExternalAuthService,
    private val tokenManager: TokenManager,
) {

    suspend fun authenticateWithGoogle(idTokenString: String): Outcome<AuthTokenPair, AuthError> {
        when (val googleResult = externalAuthService.verifyToken(idTokenString)) {
            is Outcome.Error -> {
                return Outcome.Error(
                    googleResult.code.toAuthError(),
                    googleResult.message,
                )
            }

            is Outcome.Success -> {
                val googleUser = googleResult.value
                return when (
                    val userResult = userRepository.findOrCreateUser(
                        googleUser.email,
                        googleUser.name,
                    )
                ) {
                    is Outcome.Success -> generateTokensForUser(userResult.value.id)
                    is Outcome.Error -> Outcome.Error(
                        userResult.code.toAuthError(),
                        userResult.message,
                    )
                }
            }
        }
    }

    suspend fun refreshTokens(
        refreshToken: String,
    ): Outcome<AuthTokenPair, AuthError> {
        return when (
            val userIdResult = tokenRepository.consumeRefreshToken(refreshToken)
        ) {
            is Outcome.Error -> {
                Outcome.Error(
                    userIdResult.code.toAuthError(),
                    userIdResult.message,
                )
            }

            is Outcome.Success -> {
                generateTokensForUser(userIdResult.value)
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
                refreshExpiresAt,
            )
        ) {
            is Outcome.Success -> Outcome.Success(AuthTokenPair(accessToken, refreshToken))
            is Outcome.Error -> {
                Outcome.Error(
                    saveResult.code.toAuthError(),
                    saveResult.message,
                )
            }
        }
    }
}
