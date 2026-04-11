package org.example.project.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.presentation.theme.TaxiAppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.Res
import taxiapp.composeapp.generated.resources.auth_google_button
import taxiapp.composeapp.generated.resources.auth_title
import taxiapp.composeapp.generated.resources.google_logo
import taxiapp.composeapp.generated.resources.auth_error
import taxiapp.composeapp.generated.resources.error_network
import taxiapp.composeapp.generated.resources.error_server
import taxiapp.composeapp.generated.resources.error_canceled
import taxiapp.composeapp.generated.resources.error_no_credentials
import taxiapp.composeapp.generated.resources.error_unknown

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToMain: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthScreenState.Authorized) {
            onNavigateToMain()
        }
    }

    AuthScreenContent(
        state = state,
        onLoginClick = viewModel::loginWithGoogle,
        onClearError = viewModel::clearError,
    )
}

@Composable
private fun AuthScreenContent(
    state: AuthScreenState,
    onLoginClick: () -> Unit,
    onClearError: () -> Unit,
) {
    Scaffold { padding ->
        when (state) {
            is AuthScreenState.Loading -> {
                AuthContent(
                    modifier = Modifier.padding(padding),
                    isLoading = true,
                    onLoginClick = {},
                )
            }
            is AuthScreenState.LogIn -> {
                AuthContent(
                    modifier = Modifier.padding(padding),
                    isLoading = false,
                    onLoginClick = onLoginClick,
                )
            }
            is AuthScreenState.Error -> {
                AuthContent(
                    modifier = Modifier.padding(padding),
                    isLoading = false,
                    onLoginClick = onLoginClick,
                )
                AuthErrorDialog(
                    error = state.error,
                    onDismiss = onClearError,
                )
            }
            is AuthScreenState.Authorized -> {
            }
        }
    }
}

@Composable
private fun AuthContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(Res.string.auth_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp),
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(140.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(Res.string.auth_google_button),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                        ) {
                            onLoginClick()
                        }
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
private fun AuthErrorDialog(
    error: AuthLoginError,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.auth_error),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = error.toErrorMessage(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
private fun AuthLoginError.toErrorMessage(): String = when (this) {
    is AuthLoginError.NetworkError ->
        stringResource(Res.string.error_network)
    is AuthLoginError.GoogleAuthError.NoCredentials ->
        stringResource(Res.string.error_no_credentials)
    is AuthLoginError.ServerError ->
        stringResource(Res.string.error_server)
    is AuthLoginError.Canceled ->
        stringResource(Res.string.error_canceled)
    is AuthLoginError.GoogleAuthError.Cancelled ->
        stringResource(Res.string.error_canceled)
    else -> stringResource(Res.string.error_unknown)
}

@Preview
@Composable
private fun AuthScreenPreview() {
    TaxiAppTheme {
        AuthScreenContent(
            state = AuthScreenState.LogIn,
            onLoginClick = {},
            onClearError = {},
        )
    }
}
