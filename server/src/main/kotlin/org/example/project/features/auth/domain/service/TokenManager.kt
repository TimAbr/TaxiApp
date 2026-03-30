package org.example.project.features.auth.domain.service

import kotlinx.datetime.Instant

interface TokenManager {
    fun generateAccessToken(userId: Int): String
    fun generateRefreshToken(): String
    fun getRefreshTokenExpiration(): Instant

    companion object {
        const val CLAIM_USER_ID = "userId"
        const val ACCESS_TOKEN_EXPIRATION_MS = 3600 * 1000
        const val REFRESH_TOKEN_EXPIRATION_MS = 30L * 24 * 3600 * 1000
    }
}

