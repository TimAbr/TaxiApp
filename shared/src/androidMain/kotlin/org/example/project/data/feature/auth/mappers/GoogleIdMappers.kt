package org.example.project.data.feature.auth.mappers

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import org.example.project.domain.feature.auth.repositories.AuthLoginError

fun Exception.toGoogleAuthLoginError(): AuthLoginError {
    return when (this) {

        is GetCredentialCancellationException -> AuthLoginError.GoogleAuthError.Cancelled

        is NoCredentialException -> AuthLoginError.GoogleAuthError.NoCredentials

        is GetCredentialInterruptedException -> AuthLoginError.Canceled

        is GetCredentialProviderConfigurationException -> AuthLoginError.ServerError

        is GoogleIdTokenParsingException -> AuthLoginError.GoogleAuthError.InvalidToken

        is GetCredentialException -> AuthLoginError.NetworkError

        else -> AuthLoginError.Unknown
    }
}
