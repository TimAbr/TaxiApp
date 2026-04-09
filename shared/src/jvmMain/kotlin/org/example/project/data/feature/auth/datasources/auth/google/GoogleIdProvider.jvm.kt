package org.example.project.data.feature.auth.datasources.auth.google

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.data.feature.auth.models.GoogleId
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.utils.models.Outcome
import java.awt.Desktop
import java.net.URI

class DesktopGoogleIdProvider(
    private val desktopClientId: String,
    private val desktopClientSecret: String
) : GoogleIdProvider {

    override suspend fun getId(): Outcome<GoogleId, AuthLoginError> = withContext(Dispatchers.IO) {
        try {
            val httpTransport = NetHttpTransport()
            val jsonFactory = GsonFactory.getDefaultInstance()

            val flow = GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                jsonFactory,
                desktopClientId,
                desktopClientSecret,
                listOf("email", "profile", "openid")
            ).build()

            val receiver = LocalServerReceiver.Builder().setPort(8888).build()
            val redirectUri = receiver.redirectUri

            val authUrl = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build()

            if (Desktop.isDesktopSupported() &&
                Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
            ) {
                Desktop.getDesktop().browse(URI(authUrl))
            } else {
                return@withContext Outcome.Error(AuthLoginError.Unknown)
            }

            val code = receiver.waitForCode()

            val response: GoogleTokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute()

            val idToken = response.idToken
                ?: return@withContext Outcome.Error(
                    AuthLoginError.GoogleAuthError.InvalidToken
                )

            Outcome.Success(GoogleId(idToken))
        } catch (e: Exception) {
            Outcome.Error(AuthLoginError.Unknown)
        }
    }
}