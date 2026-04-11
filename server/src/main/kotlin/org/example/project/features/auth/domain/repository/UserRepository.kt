package org.example.project.features.auth.domain.repository

import org.example.project.features.auth.domain.model.User
import org.example.project.utils.models.Outcome

enum class UserRepositoryError {
    DATABASE_ERROR,
    USER_NOT_FOUND,
}

interface UserRepository {
    suspend fun findOrCreateUser(
        email: String,
        name: String,
    ): Outcome<User, UserRepositoryError>

    suspend fun findUserById(
        id: Int,
    ): Outcome<User, UserRepositoryError>
}
