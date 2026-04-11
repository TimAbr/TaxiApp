package org.example.project.features.auth.domain.model

import kotlin.time.Duration

data class TokenManagerConfig(
    val accessTokenExpiration: Duration,
    val refreshTokenExpiration: Duration,
)
