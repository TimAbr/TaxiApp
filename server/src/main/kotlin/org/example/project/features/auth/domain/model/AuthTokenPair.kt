package org.example.project.features.auth.domain.model

data class AuthTokenPair(
    val accessToken: String,
    val refreshToken: String,
)
