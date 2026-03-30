package org.example.project.features.auth.domain.mappers

import org.example.project.features.auth.domain.repository.UserRepositoryError
import org.example.project.features.auth.domain.service.AuthError

fun UserRepositoryError.toAuthError(): AuthError = when (this) {
    UserRepositoryError.DATABASE_ERROR -> AuthError.DatabaseError
    UserRepositoryError.USER_NOT_FOUND -> AuthError.UserNotFound
}
