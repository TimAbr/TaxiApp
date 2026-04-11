package org.example.project.utils.config

import io.ktor.server.config.ApplicationConfig
import org.example.project.features.auth.domain.model.TokenManagerConfig
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

object ConfigKeys {
    const val DB_DRIVER = "storage.driverClassName"
    const val DB_URL = "storage.jdbcURL"
    const val JWT_SECRET = "jwt.secret"
    const val JWT_REALM = "jwt.realm"
    const val GOOGLE_CLIENT_IDS = "google.clientIds"
    const val JWT_ACCESS_TOKEN_EXPIRATION_HOURS = "jwt.accessTokenExpirationHours"
    const val JWT_REFRESH_TOKEN_EXPIRATION_DAYS = "jwt.refreshTokenExpirationDays"
}

data class AppConfig(
    val storage: StorageConfig,
    val jwt: JwtConfig,
    val google: GoogleConfig,
)

data class StorageConfig(
    val driver: String,
    val url: String,
)

data class JwtConfig(
    val secret: String,
    val realm: String,
    val tokenManagerConfig: TokenManagerConfig,
)

data class GoogleConfig(
    val allowedClientIds: List<String>,
)

fun ApplicationConfig.toAppConfig(): AppConfig {
    return AppConfig(
        storage = StorageConfig(
            driver = property(ConfigKeys.DB_DRIVER).getString(),
            url = property(ConfigKeys.DB_URL).getString(),
        ),
        jwt = JwtConfig(
            secret = property(ConfigKeys.JWT_SECRET).getString(),
            realm = property(ConfigKeys.JWT_REALM).getString(),
            tokenManagerConfig = TokenManagerConfig(
                accessTokenExpiration = property(ConfigKeys.JWT_ACCESS_TOKEN_EXPIRATION_HOURS).getString().toInt().hours,
                refreshTokenExpiration = property(ConfigKeys.JWT_REFRESH_TOKEN_EXPIRATION_DAYS).getString().toInt().days,
            ),
        ),
        google = GoogleConfig(
            allowedClientIds = try {
                property(ConfigKeys.GOOGLE_CLIENT_IDS).getList()
            } catch (e: Exception) {
                property(ConfigKeys.GOOGLE_CLIENT_IDS).getString()
                    .removeSurrounding("[", "]")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"").removeSurrounding("'") }
                    .filter { it.isNotEmpty() }
            }
        ),
    )
}
