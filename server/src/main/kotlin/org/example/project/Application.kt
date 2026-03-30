package org.example.project

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.request.ContentTransformationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.example.project.features.auth.data.datasources.local.DatabaseFactory
import org.example.project.features.auth.data.datasources.local.tokens.TokenLocalDataSource
import org.example.project.features.auth.data.datasources.local.users.UserLocalDataSource
import org.example.project.features.auth.api.authRoutes
import org.example.project.features.auth.data.service.GoogleAuthService
import org.example.project.features.auth.data.repository.UserRepositoryImpl
import org.example.project.features.auth.data.repository.TokenRepositoryImpl
import org.example.project.features.auth.domain.service.AuthService
import org.example.project.features.auth.domain.service.JwtTokenManager
import org.example.project.features.auth.domain.service.TokenManager
import org.example.project.utils.config.toAppConfig

const val AUTH_CONFIG_NAME = "auth-jwt"

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val appConfig = environment.config.toAppConfig()

    DatabaseFactory.init(appConfig.storage.driver, appConfig.storage.url)

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (cause is SerializationException || cause is ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid JSON")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
            }
        }
    }

    val userLocalDataSource = UserLocalDataSource()
    val tokenLocalDataSource = TokenLocalDataSource()

    val userRepository = UserRepositoryImpl(userLocalDataSource)
    val tokenRepository = TokenRepositoryImpl(tokenLocalDataSource)

    val tokenManager = JwtTokenManager(appConfig.jwt.secret, appConfig.jwt.tokenManagerConfig)

    val externalAuthService = GoogleAuthService(appConfig.google.clientId)
    val authService = AuthService(
        userRepository = userRepository,
        tokenRepository = tokenRepository,
        externalAuthService = externalAuthService,
        tokenManager = tokenManager,
    )

    install(Authentication) {
        jwt(AUTH_CONFIG_NAME) {
            realm = appConfig.jwt.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(appConfig.jwt.secret))
                    .build(),
            )
            validate { credential ->
                if (credential.payload.getClaim(TokenManager.CLAIM_USER_ID).asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Token is not valid or has expired",
                )
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        authRoutes(authService)

        authenticate(AUTH_CONFIG_NAME) {
            get("/hello-protected") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim(TokenManager.CLAIM_USER_ID)?.asInt()
                call.respondText("Hello, user $userId!")
            }
        }
    }
}
