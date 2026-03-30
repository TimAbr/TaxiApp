package org.example.project.features.auth.data.repository

import org.example.project.features.auth.data.datasources.local.users.UserDataSource
import org.example.project.features.auth.domain.model.User
import org.example.project.features.auth.domain.repository.UserRepository
import org.example.project.features.auth.domain.repository.UserRepositoryError
import org.example.project.utils.models.Outcome

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
) : UserRepository {
    override suspend fun findOrCreateUser(
        email: String,
        name: String,
    ): Outcome<User, UserRepositoryError> {
        return userDataSource.findOrCreate(email, name)
    }

    override suspend fun findUserById(
        id: Int,
    ): Outcome<User, UserRepositoryError> {
        return userDataSource.findById(id)
    }
}
