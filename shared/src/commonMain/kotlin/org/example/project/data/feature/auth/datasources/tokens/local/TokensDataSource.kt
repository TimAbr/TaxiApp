package org.example.project.data.feature.auth.datasources.tokens.local

import com.russhwolf.settings.Settings
import org.example.project.data.feature.auth.models.AccessTokenDbo
import org.example.project.data.feature.auth.models.RefreshTokenDbo

interface TokensDataSource {
    fun saveTokens(accessToken: AccessTokenDbo, refreshToken: RefreshTokenDbo)

    fun updateAccessToken(accessToken: AccessTokenDbo)
    fun updateRefreshToken(refreshToken: RefreshTokenDbo)

    fun getAccessToken(): AccessTokenDbo?
    fun getRefreshToken(): RefreshTokenDbo?

    fun clear()
}

fun createTokensDataSource(): TokensDataSource = SettingsTokensDataSource(createSettings())

expect fun createSettings(): Settings