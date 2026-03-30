package org.example.project.features.auth.api


import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.example.project.features.auth.api.dto.request.GoogleAuthRequest
import org.example.project.features.auth.api.dto.request.RefreshRequest
import org.example.project.features.auth.api.dto.response.TokenResponse
import org.example.project.features.auth.api.mappers.toHttpStatusCode
import org.example.project.features.auth.domain.service.AuthService
import org.example.project.utils.models.Outcome


fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/google") {
            val req = call.receive<GoogleAuthRequest>()
            
            when (val result = authService.authenticateWithGoogle(req.idToken)) {
                is Outcome.Success -> {
                    call.respond(
                        TokenResponse(
                            result.value.accessToken,
                            result.value.refreshToken,
                        ),
                    )
                }
                is Outcome.Error -> {
                    call.respond(
                        result.code.toHttpStatusCode(),
                        result.message ?: "Authentication failed",
                    )
                }
            }
        }

        post("/refresh") {
            val req = call.receive<RefreshRequest>()
            
            when (val result = authService.refreshTokens(req.refreshToken)) {
                is Outcome.Success -> {
                    call.respond(
                        TokenResponse(
                            result.value.accessToken,
                            result.value.refreshToken,
                        ),
                    )
                }
                is Outcome.Error -> {
                    call.respond(
                        result.code.toHttpStatusCode(),
                        result.message ?: "Token refresh failed",
                    )
                }
            }
        }
    }
}
