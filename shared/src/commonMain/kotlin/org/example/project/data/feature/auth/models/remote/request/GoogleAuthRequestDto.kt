package org.example.project.data.feature.auth.models.remote.request

import kotlinx.serialization.Serializable
import org.example.project.data.feature.auth.models.GoogleId

@Serializable
data class GoogleAuthRequestDto(val idToken: GoogleId)