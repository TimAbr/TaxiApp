package org.example.project.data.feature.auth.mappers

import io.ktor.http.HttpStatusCode
import org.example.project.domain.feature.auth.repositories.AuthLoginError

fun HttpStatusCode.toAuthLoginError(): AuthLoginError {
    return when (this) {
        HttpStatusCode.Unauthorized -> AuthLoginError.GoogleAuthError.InvalidToken
        HttpStatusCode.InternalServerError -> AuthLoginError.ServerError
        else -> AuthLoginError.Unknown
    }
}
