package org.example.project.data.feature.auth.datasources.tokens.local

import com.russhwolf.settings.Settings
import org.example.project.data.feature.auth.models.AccessToken
import org.example.project.data.feature.auth.models.RefreshToken

interface TokensDataSource {
    fun saveTokens(accessToken: AccessToken, refreshToken: RefreshToken)

    fun updateAccessToken(accessToken: AccessToken)
    fun updateRefreshToken(refreshToken: RefreshToken)

    fun getAccessToken(): AccessToken?
    fun getRefreshToken(): RefreshToken?

    fun clear()
}

expect fun createSettings(): Settings
