package org.example.project.data.feature.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(val accessToken: String, val refreshToken: String)
