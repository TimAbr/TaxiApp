package org.example.project.data.feature.auth.datasources.auth.google

import org.example.project.data.feature.auth.models.GoogleId
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.utils.models.Outcome

actual fun createGoogleSignInProvider(): GoogleIdProvider {
    return object : GoogleIdProvider {
        override suspend fun getId(): Outcome<GoogleId, AuthLoginError> {
            // TODO: Implement real Google Sign-In (Browser flow)
            // returning a mock ID for development purposes
            return Outcome.Success(GoogleId("jvm-mock-google-id-token"))
        }
    }
}