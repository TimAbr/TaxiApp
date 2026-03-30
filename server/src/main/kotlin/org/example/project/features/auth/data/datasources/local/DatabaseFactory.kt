package org.example.project.features.auth.data.datasources.local

import org.example.project.features.auth.data.datasources.local.tokens.RefreshTokensTable
import org.example.project.features.auth.data.datasources.local.users.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(
        driver: String,
        url: String,
    ) {
        val database = Database.connect(url, driver)

        transaction(database) {
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(RefreshTokensTable)
        }
    }
}
