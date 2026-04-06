package org.example.project.data.feature.auth.mappers

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import org.example.project.domain.feature.auth.repositories.AuthLoginError

fun Exception.toGoogleAuthLoginError(): AuthLoginError {
    return when (this) {
        is GetCredentialCancellationException -> AuthLoginError.GoogleAuthError.Cancelled
        is NoCredentialException -> AuthLoginError.GoogleAuthError.NoCredentials
        is GetCredentialException -> {
            if (this.message?.contains("No credentials available", ignoreCase = true) == true) {
                AuthLoginError.GoogleAuthError.NoCredentials
            } else {
                AuthLoginError.NetworkError
            }
        }
        else -> {
            println("GoogleIdMappers: UNKNOWN EXCEPTION TYPE: ${this::class.simpleName}")
            AuthLoginError.Unknown
        }
    }
}
