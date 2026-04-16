package org.example.project.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.presentation.theme.TaxiAppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.Res
import taxiapp.composeapp.generated.resources.cancel_button
import taxiapp.composeapp.generated.resources.current_location_title
import taxiapp.composeapp.generated.resources.error_gps_disabled
import taxiapp.composeapp.generated.resources.error_no_permission
import taxiapp.composeapp.generated.resources.error_service_unavailable
import taxiapp.composeapp.generated.resources.error_timeout
import taxiapp.composeapp.generated.resources.finding_location
import taxiapp.composeapp.generated.resources.google_logo
import taxiapp.composeapp.generated.resources.lat_label
import taxiapp.composeapp.generated.resources.location_error_unknown
import taxiapp.composeapp.generated.resources.logout_button
import taxiapp.composeapp.generated.resources.logout_confirm_message
import taxiapp.composeapp.generated.resources.logout_confirm_title
import taxiapp.composeapp.generated.resources.lon_label

@Composable
fun MainScreen(
    state: MainScreenState,
    events: Flow<MainEvent>,
    onLogoutSuccess: () -> Unit,
    onPermissionDenied: () -> Unit,
    onLogoutRequest: () -> Unit,
    onLogoutConfirm: () -> Unit,
    onLogoutDismiss: () -> Unit,
) {
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is MainEvent.LogoutSuccess -> onLogoutSuccess()
                is MainEvent.NavigateToLogin -> onLogoutSuccess()
            }
        }
    }

    LaunchedEffect(state.locationError) {
        if (state.locationError == LocationError.NO_PERMISSION) {
            onPermissionDenied()
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LocationCard(
                    location = state.location,
                )

                if (state.locationError != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorMessage(error = state.locationError)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                    ) {
                        onLogoutRequest()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.google_logo),
                    contentDescription = stringResource(Res.string.logout_button),
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }

        LogoutBottomSheet(
            state = state.sheetState,
            onLogout = onLogoutConfirm,
            onDismiss = onLogoutDismiss,
        )
    }
}

@Composable
private fun LocationCard(
    location: LocationCoordinates?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (location != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(Res.string.current_location_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CoordinateItem(
                        label = stringResource(Res.string.lat_label),
                        value = location.lat,
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp, 1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    CoordinateItem(
                        label = stringResource(Res.string.lon_label),
                        value = location.lon,
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(Res.string.finding_location),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CoordinateItem(
    label: String,
    value: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorMessage(
    error: LocationError,
    modifier: Modifier = Modifier
) {
    val messageRes = when (error) {
        LocationError.GPS_DISABLED -> Res.string.error_gps_disabled
        LocationError.TIMEOUT -> Res.string.error_timeout
        LocationError.SERVICE_UNAVAILABLE -> Res.string.error_service_unavailable
        LocationError.UNKNOWN -> Res.string.location_error_unknown
        LocationError.NO_PERMISSION -> Res.string.error_no_permission
    }

    Text(
        text = stringResource(messageRes),
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogoutBottomSheet(
    state: MainSheetState,
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(state) {
        if (state is MainSheetState.LogoutConfirmation) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (sheetState.isVisible || state is MainSheetState.LogoutConfirmation) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.logout_confirm_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.logout_confirm_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ElevatedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    ) {
                        Text(
                            text = stringResource(Res.string.logout_button),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    ElevatedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Text(
                            text = stringResource(Res.string.cancel_button),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(name = "Default/Loading")
@Composable
private fun MainScreenPreview() {
    TaxiAppTheme {
        MainScreen(
            state = MainScreenState(),
            events = emptyFlow(),
            onLogoutSuccess = {},
            onPermissionDenied = {},
            onLogoutRequest = {},
            onLogoutConfirm = {},
            onLogoutDismiss = {},
        )
    }
}

@Preview(name = "Success Location")
@Composable
private fun PreviewSuccess() {
    TaxiAppTheme {
        MainScreen(
            state = MainScreenState(
                location = LocationCoordinates(55.755829, 37.617299)
            ),
            events = emptyFlow(),
            onLogoutSuccess = {},
            onPermissionDenied = {},
            onLogoutRequest = {},
            onLogoutConfirm = {},
            onLogoutDismiss = {},
        )
    }
}

@Preview(name = "Location Error")
@Composable
private fun PreviewError() {
    TaxiAppTheme {
        MainScreen(
            state = MainScreenState(
                locationError = LocationError.GPS_DISABLED
            ),
            events = emptyFlow(),
            onLogoutSuccess = {},
            onPermissionDenied = {},
            onLogoutRequest = {},
            onLogoutConfirm = {},
            onLogoutDismiss = {},
        )
    }
}