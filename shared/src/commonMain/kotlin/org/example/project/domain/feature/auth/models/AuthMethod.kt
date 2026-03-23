package org.example.project.domain.feature.auth.models

sealed interface AuthMethod {
    data object Google : AuthMethod
}
