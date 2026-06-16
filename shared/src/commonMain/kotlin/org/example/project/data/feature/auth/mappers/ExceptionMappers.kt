package org.example.project.data.feature.auth.mappers

import kotlinx.io.IOException
import org.example.project.domain.feature.auth.repositories.AuthLoginError

fun Exception.toAuthLoginError(): AuthLoginError {
    return when (this) {
        is IOException -> AuthLoginError.NetworkError
        else -> AuthLoginError.Unknown
    }
}
