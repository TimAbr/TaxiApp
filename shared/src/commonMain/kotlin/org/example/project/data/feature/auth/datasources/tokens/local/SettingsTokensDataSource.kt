package org.example.project.data.feature.auth.datasources.tokens.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.example.project.data.feature.auth.models.AccessTokenDbo
import org.example.project.data.feature.auth.models.RefreshTokenDbo

class SettingsTokensDataSource(
    private val settings: Settings
) : TokensDataSource {
    private val accessTokenKey = "access_token"
    private val refreshTokenKey = "refresh_token"

    override fun saveTokens(accessToken: AccessTokenDbo, refreshToken: RefreshTokenDbo) {
        settings[accessTokenKey] = accessToken.value
        settings[refreshTokenKey] = refreshToken.value
    }

    override fun updateAccessToken(accessToken: AccessTokenDbo) {
        settings[accessTokenKey] = accessToken.value
    }

    override fun updateRefreshToken(refreshToken: RefreshTokenDbo) {
        settings[refreshTokenKey] = refreshToken.value
    }

    override fun getAccessToken(): AccessTokenDbo? {
        val token = settings.getString(accessTokenKey, "")
        return if (token.isNotEmpty()) AccessTokenDbo(token) else null
    }

    override fun getRefreshToken(): RefreshTokenDbo? {
        val token = settings.getString(refreshTokenKey, "")
        return if (token.isNotEmpty()) RefreshTokenDbo(token) else null
    }

    override fun clear() {
        settings.remove(accessTokenKey)
        settings.remove(refreshTokenKey)
    }
}
