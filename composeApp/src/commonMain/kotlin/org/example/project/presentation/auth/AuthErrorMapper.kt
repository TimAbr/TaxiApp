package org.example.project.presentation.auth

import androidx.compose.runtime.Composable
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.Res
import taxiapp.composeapp.generated.resources.error_canceled
import taxiapp.composeapp.generated.resources.error_network
import taxiapp.composeapp.generated.resources.error_no_credentials
import taxiapp.composeapp.generated.resources.error_server
import taxiapp.composeapp.generated.resources.error_unknown

@Composable
fun AuthLoginError.toErrorMessage(): String = when (this) {
    is AuthLoginError.NetworkError ->
        stringResource(Res.string.error_network)
    is AuthLoginError.GoogleAuthError.NoCredentials ->
        stringResource(Res.string.error_no_credentials)
    is AuthLoginError.ServerError ->
        stringResource(Res.string.error_server)
    is AuthLoginError.Canceled ->
        stringResource(Res.string.error_canceled)
    else ->
        stringResource(Res.string.error_unknown)
}
