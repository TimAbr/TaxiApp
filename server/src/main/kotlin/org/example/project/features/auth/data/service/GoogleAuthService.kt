package org.example.project.features.auth.data.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.example.project.features.auth.domain.model.ExternalUserInfo
import org.example.project.features.auth.domain.external.ExternalAuthService
import org.example.project.features.auth.domain.external.ExternalAuthError
import org.example.project.utils.models.Outcome
import java.util.Collections

class GoogleAuthService(
    googleClientId: String,
) : ExternalAuthService {

    private val verifier = GoogleIdTokenVerifier
        .Builder(NetHttpTransport(), GsonFactory.getDefaultInstance())
        .setAudience(Collections.singletonList(googleClientId))
        .build()

    override suspend fun verifyToken(
        idToken: String,
    ): Outcome<ExternalUserInfo, ExternalAuthError> {
        return try {
            val token = verifier.verify(idToken)
            if (token != null) {
                val payload = token.payload
                val email = payload.email ?: return Outcome.Error(
                    ExternalAuthError.INVALID_TOKEN,
                    "Google ID Token does not contain an email",
                )
                Outcome.Success(
                    ExternalUserInfo(
                        email = email,
                        name = (payload[FIELD_NAME] as? String) ?: payload.email,
                    ),
                )
            } else {
                Outcome.Error(
                    ExternalAuthError.INVALID_TOKEN,
                    "Google ID Token verification failed",
                )
            }
        } catch (e: java.io.IOException) {
            Outcome.Error(ExternalAuthError.NETWORK_ERROR, e.message)
        } catch (e: Exception) {
            Outcome.Error(ExternalAuthError.UNKNOWN_ERROR, e.message)
        }
    }

    companion object {
        private const val FIELD_NAME = "name"
    }
}
