package org.example.project.data.feature.auth.mappers

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import org.example.project.domain.feature.auth.repositories.AuthLoginError

fun Exception.toGoogleAuthLoginError(): AuthLoginError {
    return when (this) {
        is GetCredentialCancellationException -> AuthLoginError.GoogleAuthError.Cancelled
        is GetCredentialException -> AuthLoginError.NetworkError
        else -> AuthLoginError.Unknown
    }
}
