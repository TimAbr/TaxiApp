package org.example.project.presentation.main

import androidx.compose.runtime.Composable
import org.example.project.domain.feature.location.repository.LocationError
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.*

@Composable
fun LocationError.toErrorMessage(): String = when (this) {
    LocationError.GPS_DISABLED ->
        stringResource(Res.string.error_gps_disabled)
    LocationError.TIMEOUT ->
        stringResource(Res.string.error_timeout)
    LocationError.SERVICE_UNAVAILABLE ->
        stringResource(Res.string.error_service_unavailable)
    LocationError.UNKNOWN ->
        stringResource(Res.string.location_error_unknown)
    LocationError.NO_PERMISSION ->
        stringResource(Res.string.error_no_permission)
}
