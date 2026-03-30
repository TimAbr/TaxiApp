package org.example.project.data.feature.auth.models.remote.response

import kotlinx.serialization.Serializable
import org.example.project.data.feature.auth.models.AccessToken
import org.example.project.data.feature.auth.models.RefreshToken

@Serializable
data class TokenResponseDto(
    val accessToken: AccessToken, 
    val refreshToken: RefreshToken
)
