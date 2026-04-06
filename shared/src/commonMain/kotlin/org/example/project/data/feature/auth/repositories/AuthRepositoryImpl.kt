package org.example.project.data.feature.auth.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.example.project.data.feature.auth.datasources.auth.google.GoogleIdProvider
import org.example.project.data.feature.auth.datasources.auth.remote.AuthRemoteDataSource
import org.example.project.data.feature.auth.models.remote.request.GoogleAuthRequestDto
import org.example.project.domain.feature.auth.models.AccessToken
import org.example.project.domain.feature.auth.models.AuthMethod
import org.example.project.domain.feature.auth.models.RefreshToken
import org.example.project.domain.feature.auth.models.TokenPair
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.domain.feature.auth.repositories.AuthLogoutError
import org.example.project.domain.feature.auth.repositories.AuthRepository
import org.example.project.domain.feature.auth.repositories.TokenRepository
import org.example.project.utils.models.Outcome

class AuthRepositoryImpl(
    private val googleIdProvider: GoogleIdProvider,
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenRepository: TokenRepository,
) : AuthRepository {
    
    private val _isAuthorized = MutableStateFlow(tokenRepository.getAccessToken() != null)
    override val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    override suspend fun login(method: AuthMethod): Outcome<Unit, AuthLoginError> = withContext(
        Dispatchers.Default) {
        when (method) {
            is AuthMethod.Google -> {
                val googleResult = googleIdProvider.getId()
                
                if (googleResult is Outcome.Error) {
                    return@withContext Outcome.Error(
                        code = googleResult.code,
                        message = googleResult.message,
                    )
                }

                val idToken = (googleResult as Outcome.Success).value
                val remoteResult = remoteDataSource
                    .authenticateWithGoogle(GoogleAuthRequestDto(idToken))
                
                when (remoteResult) {
                    is Outcome.Success -> {
                        val tokens = remoteResult.value
                        tokenRepository.saveTokens(
                            TokenPair(
                                accessToken = AccessToken(tokens.accessToken.value),
                                refreshToken = RefreshToken(tokens.refreshToken.value),
                            ),
                        )
                        _isAuthorized.value = true
                        Outcome.Success(Unit)
                    }
                    is Outcome.Error -> {
                        Outcome.Error(
                            code = remoteResult.code,
                            message = remoteResult.message,
                        )
                    }
                }
            }
        }
    }

    override suspend fun logout(): Outcome<Unit, AuthLogoutError> {
        tokenRepository.clear()
        _isAuthorized.value = false
        return Outcome.Success(Unit)
    }
}
