package org.example.project.features.auth.domain.mappers

import org.example.project.features.auth.domain.external.ExternalAuthError
import org.example.project.features.auth.domain.service.AuthError

fun ExternalAuthError.toAuthError(): AuthError = when (this) {
    ExternalAuthError.INVALID_TOKEN -> AuthError.InvalidToken
    ExternalAuthError.NETWORK_ERROR -> AuthError.ExternalServiceError
    ExternalAuthError.UNKNOWN_ERROR -> AuthError.ExternalServiceError
}
