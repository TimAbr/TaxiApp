package org.example.project.features.auth.data.datasources.local.users

import org.example.project.features.auth.domain.model.User
import org.example.project.features.auth.domain.repository.UserRepositoryError
import org.example.project.utils.models.Outcome

interface UserDataSource {
    fun findOrCreate(
        email: String,
        name: String,
    ): Outcome<User, UserRepositoryError>

    fun findById(
        id: Int,
    ): Outcome<User, UserRepositoryError>
}
