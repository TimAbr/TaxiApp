package org.example.project.data.feature.auth.datasources.auth.google

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import org.example.project.data.feature.auth.models.GoogleId
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.utils.models.Outcome
import org.example.project.shared.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.data.feature.auth.mappers.toGoogleAuthLoginError

class AndroidGoogleIdProvider(
    private val context: Context,
    private val webClientId: String = BuildConfig.GOOGLE_WEB_CLIENT_ID,
) : GoogleIdProvider {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun getId(): Outcome<GoogleId, AuthLoginError> =
        withContext(Dispatchers.Main) {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request,
                )

                handleSignIn(result)
            } catch (e: Exception) {
                Outcome.Error(e.toGoogleAuthLoginError())
            }
        }

    private fun handleSignIn(result: GetCredentialResponse): Outcome<GoogleId, AuthLoginError> {
        val credential = result.credential

        return if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Outcome.Success(GoogleId(googleIdTokenCredential.idToken))
            } catch (e: Exception) {
                Outcome.Error(AuthLoginError.Unknown)
            }
        } else {
            Outcome.Error(AuthLoginError.Unknown)
        }
    }
}

actual fun createGoogleSignInProvider(): GoogleIdProvider {
    TODO("")
}
