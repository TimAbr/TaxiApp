package org.example.project.features.auth.data.datasources.local.tokens

import kotlinx.datetime.LocalDateTime

data class RefreshTokenEntity(
    val id: Int,
    val userId: Int,
    val tokenValue: String,
    val expiresAt: LocalDateTime
)