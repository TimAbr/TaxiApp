package org.example.project.data.feature.auth.datasources.auth.google

import org.example.project.data.feature.auth.models.GoogleId
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.utils.models.Outcome

interface GoogleIdProvider {
    suspend fun getId(): Outcome<GoogleId, AuthLoginError>
}
