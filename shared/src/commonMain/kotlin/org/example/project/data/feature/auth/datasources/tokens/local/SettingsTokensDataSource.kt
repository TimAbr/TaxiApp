package org.example.project.data.feature.auth.datasources.tokens.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import org.example.project.data.feature.auth.models.AccessToken
import org.example.project.data.feature.auth.models.RefreshToken

class SettingsTokensDataSource(
    private val settings: Settings
) : TokensDataSource {
    private val accessTokenKey = "access_token"
    private val refreshTokenKey = "refresh_token"

    override fun saveTokens(accessToken: AccessToken, refreshToken: RefreshToken) {
        settings[accessTokenKey] = accessToken.value
        settings[refreshTokenKey] = refreshToken.value
    }

    override fun updateAccessToken(accessToken: AccessToken) {
        settings[accessTokenKey] = accessToken.value
    }

    override fun updateRefreshToken(refreshToken: RefreshToken) {
        settings[refreshTokenKey] = refreshToken.value
    }

    override fun getAccessToken(): AccessToken? {
        val token = settings.getString(accessTokenKey, "")
        return if (token.isNotEmpty()) AccessToken(token) else null
    }

    override fun getRefreshToken(): RefreshToken? {
        val token = settings.getString(refreshTokenKey, "")
        return if (token.isNotEmpty()) RefreshToken(token) else null
    }

    override fun clear() {
        settings.remove(accessTokenKey)
        settings.remove(refreshTokenKey)
    }
}
