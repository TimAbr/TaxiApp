package org.example.project.features.auth.data.datasources.local.tokens

import org.example.project.features.auth.data.datasources.local.users.UsersTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlinx.datetime.LocalDateTime

object RefreshTokensTable : IntIdTable("refresh_tokens") {
    private const val TOKEN_VALUE_LENGTH = 255

    val userId = reference(
        "user_id",
        UsersTable,
        onDelete = ReferenceOption.CASCADE
    )
    val tokenValue = varchar("token_value", TOKEN_VALUE_LENGTH).uniqueIndex()
    val expiresAt: Column<LocalDateTime> = datetime("expires_at")
}