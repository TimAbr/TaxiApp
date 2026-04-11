package org.example.project.features.auth.domain.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.example.project.features.auth.domain.model.TokenManagerConfig
import java.util.UUID

class JwtTokenManager(
    private val jwtSecret: String,
    private val config: TokenManagerConfig,
) : TokenManager {

    override fun generateAccessToken(userId: Int): String {
        val expirationInstant = generateAccessTokenExpiration()
        return JWT.create()
            .withClaim(TokenManager.CLAIM_USER_ID, userId)
            .withExpiresAt(expirationInstant.toJavaInstant())
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    override fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }

    override fun getRefreshTokenExpiration(): Instant {
        return Clock.System.now().plus(config.refreshTokenExpiration)
    }

    private fun generateAccessTokenExpiration() =
        Clock.System.now().plus(config.accessTokenExpiration)


}
