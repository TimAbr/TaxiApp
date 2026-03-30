package org.example.project.features.auth.data.datasources.local.tokens

import kotlinx.datetime.Instant
import org.example.project.features.auth.data.datasources.local.users.UsersTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object RefreshTokensTable : IntIdTable("refresh_tokens") {
    val userId = reference(
        "user_id",
        UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val tokenValue = varchar("token_value", TOKEN_VALUE_LENGTH).uniqueIndex()
    val expiresAt: Column<Instant> = timestamp("expires_at")

    private const val TOKEN_VALUE_LENGTH = 255
}
