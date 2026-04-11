package org.example.project.features.auth.api.mappers

import io.ktor.http.HttpStatusCode
import org.example.project.features.auth.domain.service.AuthError

fun AuthError.toHttpStatusCode(): HttpStatusCode = when (this) {
    AuthError.InvalidToken -> HttpStatusCode.Unauthorized
    AuthError.DatabaseError -> HttpStatusCode.InternalServerError
    AuthError.ExternalServiceError -> HttpStatusCode.BadGateway
    AuthError.UserNotFound -> HttpStatusCode.NotFound
}
