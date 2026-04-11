package org.example.project.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    LaunchedEffect(state.isAuthorized) {
        if (state.isAuthorized) {
            onNavigateToMain()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                onClick = { viewModel.loginWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 20.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                enabled = !state.isLoading,
            ) {
                if (state.isLoading) {
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

    state.error?.let { error ->
        val errorMessage = when (error) {
            is AuthLoginError.NetworkError -> stringResource(Res.string.error_network)
            is AuthLoginError.GoogleAuthError.NoCredentials -> stringResource(Res.string.error_no_credentials)
            is AuthLoginError.ServerError -> stringResource(Res.string.error_server)
            is AuthLoginError.Canceled -> stringResource(Res.string.error_canceled)
            is AuthLoginError.GoogleAuthError.Cancelled -> stringResource(Res.string.error_canceled)
            else -> stringResource(Res.string.error_unknown)
        }

        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(stringResource(Res.string.auth_error))
            },
            text = {
                Text(errorMessage)
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearError() },
                ) {
                    Text("OK")
                }
            },
        )
    }
}
