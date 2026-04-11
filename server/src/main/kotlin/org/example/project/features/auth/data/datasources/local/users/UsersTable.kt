package org.example.project.features.auth.data.datasources.local.users

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object UsersTable : IntIdTable("users") {
    val email: Column<String> = varchar("email", DEFAULT_VARCHAR_LENGTH).uniqueIndex()
    val name: Column<String> = varchar("name", DEFAULT_VARCHAR_LENGTH)

    private const val DEFAULT_VARCHAR_LENGTH = 255
}
