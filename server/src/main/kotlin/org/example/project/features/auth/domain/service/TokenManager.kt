package org.example.project.features.auth.domain.service

import kotlinx.datetime.Instant

interface TokenManager {
    fun generateAccessToken(userId: Int): String
    fun generateRefreshToken(): String
    fun getRefreshTokenExpiration(): Instant

    companion object {
        const val CLAIM_USER_ID = "userId"
    }
}
