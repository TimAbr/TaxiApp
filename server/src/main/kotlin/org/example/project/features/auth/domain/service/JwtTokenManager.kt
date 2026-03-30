package org.example.project.features.auth.domain.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import java.util.Date
import java.util.UUID

class JwtTokenManager(
    private val jwtSecret: String,
) : TokenManager {

    override fun generateAccessToken(userId: Int): String {
        return JWT.create()
            .withClaim(TokenManager.CLAIM_USER_ID, userId)
            .withExpiresAt(Date(System.currentTimeMillis() + TokenManager.ACCESS_TOKEN_EXPIRATION_MS))
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    override fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }

    override fun getRefreshTokenExpiration(): Instant {
        return Clock.System.now().plus(
            TokenManager.REFRESH_TOKEN_EXPIRATION_MS,
            DateTimeUnit.Companion.MILLISECOND,
        )
    }
}