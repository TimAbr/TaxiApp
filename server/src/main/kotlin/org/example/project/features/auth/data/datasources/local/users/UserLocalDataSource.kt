package org.example.project.features.auth.data.datasources.local.users

import org.example.project.features.auth.domain.model.User
import org.example.project.features.auth.domain.repository.UserRepositoryError
import org.example.project.utils.models.Outcome
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserLocalDataSource : UserDataSource {
    override fun findOrCreate(
        email: String,
        name: String,
    ): Outcome<User, UserRepositoryError> = try {
        transaction {
            val user = UsersTable
                .selectAll()
                .where { UsersTable.email eq email }
                .singleOrNull()
                ?.let {
                    User(
                        it[UsersTable.id].value,
                        it[UsersTable.email],
                        it[UsersTable.name],
                    )
                } ?: run {
                val id = UsersTable.insertAndGetId {
                    it[UsersTable.email] = email
                    it[UsersTable.name] = name
                }
                User(id.value, email, name)
            }
            Outcome.Success(user)
        }
    } catch (e: Exception) {
        Outcome.Error(UserRepositoryError.DATABASE_ERROR, e.message)
    }

    override fun findById(
        id: Int,
    ): Outcome<User, UserRepositoryError> = try {
        transaction {
            val user = UsersTable
                .selectAll()
                .where { UsersTable.id eq id }
                .singleOrNull()
                ?.let {
                    User(
                        it[UsersTable.id].value,
                        it[UsersTable.email],
                        it[UsersTable.name],
                    )
                }
            if (user != null) {
                Outcome.Success(user)
            } else {
                Outcome.Error(
                    UserRepositoryError.USER_NOT_FOUND,
                    "User with ID $id not found",
                )
            }
        }
    } catch (e: Exception) {
        Outcome.Error(UserRepositoryError.DATABASE_ERROR, e.message)
    }
}