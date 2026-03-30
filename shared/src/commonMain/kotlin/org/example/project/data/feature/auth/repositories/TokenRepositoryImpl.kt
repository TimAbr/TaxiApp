package org.example.project.data.feature.auth.repositories

import org.example.project.data.feature.auth.datasources.tokens.local.TokensDataSource
import org.example.project.data.feature.auth.models.AccessToken as DataAccessToken
import org.example.project.data.feature.auth.models.RefreshToken as DataRefreshToken
import org.example.project.domain.feature.auth.models.AccessToken
import org.example.project.domain.feature.auth.models.RefreshToken
import org.example.project.domain.feature.auth.models.TokenPair
import org.example.project.domain.feature.auth.repositories.TokenRepository

class TokenRepositoryImpl(
    private val tokensDataSource: TokensDataSource
): TokenRepository {
    override fun saveTokens(tokenPair: TokenPair) {
        val accessTokenData = DataAccessToken(tokenPair.accessToken.value)
        val refreshTokenData = DataRefreshToken(tokenPair.refreshToken.value)
        tokensDataSource.saveTokens(accessTokenData, refreshTokenData)
    }

    override fun updateAccessToken(accessToken: AccessToken) {
        tokensDataSource.updateAccessToken(DataAccessToken(accessToken.value))
    }

    override fun updateRefreshToken(refreshToken: RefreshToken) {
        tokensDataSource.updateRefreshToken(DataRefreshToken(refreshToken.value))
    }

    override fun getAccessToken(): AccessToken? {
        return tokensDataSource.getAccessToken()?.let { AccessToken(it.value) }
    }

    override fun getRefreshToken(): RefreshToken? {
        return tokensDataSource.getRefreshToken()?.let { RefreshToken(it.value) }
    }

    override fun getTokenPair(): TokenPair? {
        val accessToken = getAccessToken() ?: return null
        val refreshToken = getRefreshToken() ?: return null
        return TokenPair(accessToken, refreshToken)
    }

    override fun clear() {
        tokensDataSource.clear()
    }
}
