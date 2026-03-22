package org.example.project.features.auth.api

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.features.auth.api.dto.request.GoogleAuthRequest
import org.example.project.features.auth.api.dto.request.RefreshRequest
import org.example.project.features.auth.api.dto.response.TokenResponse
import org.example.project.features.auth.domain.service.AuthService
import org.example.project.features.auth.domain.service.AuthError
import org.example.project.utils.models.Outcome

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/google") {
            val req = try { call.receive<GoogleAuthRequest>() } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid JSON")
                return@post
            }
            
            when (val result = authService.authenticateWithGoogle(req.idToken)) {
                is Outcome.Success -> {
                    call.respond(TokenResponse(result.value.accessToken, result.value.refreshToken))
                }
                is Outcome.Error -> {
                    val status = when (result.code) {
                        is AuthError.InvalidToken -> HttpStatusCode.Unauthorized
                        is AuthError.DatabaseError -> HttpStatusCode.InternalServerError
                        is AuthError.ExternalServiceError -> HttpStatusCode.BadGateway
                        else -> HttpStatusCode.InternalServerError
                    }
                    call.respond(status, result.message ?: "Authentication failed")
                }
            }
        }

        post("/refresh") {
            val req = try { call.receive<RefreshRequest>() } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid JSON")
                return@post
            }
            
            when (val result = authService.refreshTokens(req.refreshToken)) {
                is Outcome.Success -> {
                    call.respond(TokenResponse(result.value.accessToken, result.value.refreshToken))
                }
                is Outcome.Error -> {
                    val status = when (result.code) {
                        is AuthError.InvalidToken -> HttpStatusCode.Unauthorized
                        is AuthError.DatabaseError -> HttpStatusCode.InternalServerError
                        else -> HttpStatusCode.InternalServerError
                    }
                    call.respond(status, result.message ?: "Token refresh failed")
                }
            }
        }
    }
}
