package org.example.project.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.Res
import taxiapp.composeapp.generated.resources.auth_google_button
import taxiapp.composeapp.generated.resources.auth_title
import taxiapp.composeapp.generated.resources.google_logo_placeholder
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

    Scaffold { padding ->
        when (val currentState = state) {
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
                    onLoginClick = viewModel::loginWithGoogle,
                )
            }
            is AuthScreenState.Error -> {
                AuthContent(
                    modifier = Modifier.padding(padding),
                    isLoading = false,
                    onLoginClick = viewModel::loginWithGoogle,
                )
                AuthErrorDialog(
                    error = currentState.error,
                    onDismiss = viewModel::clearError,
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(Res.string.auth_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 40.dp),
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.google_logo_placeholder),
                contentDescription = "Google Logo",
                modifier = Modifier.size(120.dp),
            )
        }

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 20.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(Res.string.auth_google_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
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
            Text(stringResource(Res.string.auth_error))
        },
        text = {
            Text(error.toErrorMessage())
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("OK")
            }
        },
    )
}

@Composable
private fun AuthLoginError.toErrorMessage(): String = when (this) {
    is AuthLoginError.NetworkError -> stringResource(Res.string.error_network)
    is AuthLoginError.GoogleAuthError.NoCredentials -> stringResource(Res.string.error_no_credentials)
    is AuthLoginError.ServerError -> stringResource(Res.string.error_server)
    is AuthLoginError.Canceled -> stringResource(Res.string.error_canceled)
    is AuthLoginError.GoogleAuthError.Cancelled -> stringResource(Res.string.error_canceled)
    else -> stringResource(Res.string.error_unknown)
}
