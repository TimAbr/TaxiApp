package org.example.project.features.auth.domain.external

import org.example.project.features.auth.domain.model.ExternalUserInfo
import org.example.project.utils.models.Outcome

enum class ExternalAuthError {
    INVALID_TOKEN,
    NETWORK_ERROR,
    UNKNOWN_ERROR,
}

interface ExternalAuthService {
    suspend fun verifyToken(
        idToken: String,
    ): Outcome<ExternalUserInfo, ExternalAuthError>
}
