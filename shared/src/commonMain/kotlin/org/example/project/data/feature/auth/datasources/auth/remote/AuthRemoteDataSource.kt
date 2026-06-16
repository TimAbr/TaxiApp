package org.example.project.data.feature.auth.datasources.auth.remote

import org.example.project.data.feature.auth.models.remote.request.GoogleAuthRequestDto
import org.example.project.data.feature.auth.models.remote.request.RefreshRequestDto
import org.example.project.data.feature.auth.models.remote.response.TokenResponseDto
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.utils.models.Outcome

interface AuthRemoteDataSource {
    suspend fun authenticateWithGoogle(request: GoogleAuthRequestDto): Outcome<TokenResponseDto, AuthLoginError>
    suspend fun refreshTokens(request: RefreshRequestDto): Outcome<TokenResponseDto, AuthLoginError>
}
