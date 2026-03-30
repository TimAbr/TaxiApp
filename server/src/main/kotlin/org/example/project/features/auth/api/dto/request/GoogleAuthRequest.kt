package org.example.project.features.auth.api.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthRequest(
    val idToken: String,
)