package org.example.project.data.feature.auth.models.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(val refreshToken: String)
