package org.example.project.features.auth.data.datasources.local.tokens

import kotlinx.datetime.Instant

data class RefreshTokenEntity(
    val id: Int,
    val userId: Int,
    val tokenValue: String,
    val expiresAt: Instant,
)