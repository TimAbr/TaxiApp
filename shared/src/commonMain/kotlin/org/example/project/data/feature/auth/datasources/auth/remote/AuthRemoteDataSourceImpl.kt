package org.example.project.data.feature.auth.datasources.auth.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.example.project.data.feature.auth.mappers.toAuthLoginError
import org.example.project.data.feature.auth.models.remote.request.GoogleAuthRequestDto
import org.example.project.data.feature.auth.models.remote.request.RefreshRequestDto
import org.example.project.data.feature.auth.models.remote.response.TokenResponseDto
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.utils.models.Outcome

class AuthRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseURL: String
) : AuthRemoteDataSource {

    override suspend fun authenticateWithGoogle(request: GoogleAuthRequestDto): Outcome<TokenResponseDto, AuthLoginError> {
        return try {
            val response = httpClient.post("$baseURL/auth/google") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                Outcome.Success(response.body<TokenResponseDto>())
            } else {
                Outcome.Error(response.status.toAuthLoginError())
            }
        }
        catch (e: Exception) {
            Outcome.Error(e.toAuthLoginError())
        }
    }

    override suspend fun refreshTokens(request: RefreshRequestDto): Outcome<TokenResponseDto, AuthLoginError> {
        return try {
            val response = httpClient.post("$baseURL/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                Outcome.Success(response.body<TokenResponseDto>())
            } else {
                Outcome.Error(response.status.toAuthLoginError())
            }
        }
        catch (e: Exception) {
            Outcome.Error(e.toAuthLoginError())
        }
    }
}
