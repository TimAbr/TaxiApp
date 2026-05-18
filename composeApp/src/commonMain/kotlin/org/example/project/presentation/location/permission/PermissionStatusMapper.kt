package org.example.project.presentation.location.permission

import androidx.compose.runtime.Composable
import org.example.project.domain.feature.location.models.PermissionStatus
import org.jetbrains.compose.resources.stringResource
import taxiapp.composeapp.generated.resources.*

@Composable
fun PermissionStatus.toTitle(): String = when (this) {
    PermissionStatus.DENIED_ALWAYS ->
        stringResource(Res.string.location_permission_denied_always_title)
    PermissionStatus.LOW_ACCURACY ->
        stringResource(Res.string.location_permission_low_accuracy_title)
    PermissionStatus.DENIED ->
        stringResource(Res.string.location_permission_denied_title)
    else ->
        stringResource(Res.string.location_permission_title)
}

@Composable
fun PermissionStatus.toDescription(): String = when (this) {
    PermissionStatus.DENIED_ALWAYS ->
        stringResource(Res.string.location_permission_denied_always_description)
    PermissionStatus.LOW_ACCURACY ->
        stringResource(Res.string.location_permission_low_accuracy_description)
    PermissionStatus.DENIED ->
        stringResource(Res.string.location_permission_denied_description)
    else ->
        stringResource(Res.string.location_permission_description)
}

@Composable
fun PermissionStatus.toButtonText(): String = if (this == PermissionStatus.DENIED_ALWAYS) {
    stringResource(Res.string.location_permission_settings_button)
} else {
    stringResource(Res.string.location_permission_button)
}
