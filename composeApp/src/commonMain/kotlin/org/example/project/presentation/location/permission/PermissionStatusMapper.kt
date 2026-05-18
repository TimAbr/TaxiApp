package org.example.project.presentation.location.permission

import org.example.project.domain.feature.location.models.PermissionStatus
import org.jetbrains.compose.resources.StringResource
import taxiapp.composeapp.generated.resources.Res
import taxiapp.composeapp.generated.resources.location_permission_denied_always_title
import taxiapp.composeapp.generated.resources.location_permission_low_accuracy_title
import taxiapp.composeapp.generated.resources.location_permission_denied_title
import taxiapp.composeapp.generated.resources.location_permission_title
import taxiapp.composeapp.generated.resources.location_permission_denied_always_description
import taxiapp.composeapp.generated.resources.location_permission_low_accuracy_description
import taxiapp.composeapp.generated.resources.location_permission_denied_description
import taxiapp.composeapp.generated.resources.location_permission_description
import taxiapp.composeapp.generated.resources.location_permission_settings_button
import taxiapp.composeapp.generated.resources.location_permission_button

val PermissionStatus.titleRes: StringResource
    get() = when (this) {
        PermissionStatus.DENIED_ALWAYS -> Res.string.location_permission_denied_always_title
        PermissionStatus.LOW_ACCURACY -> Res.string.location_permission_low_accuracy_title
        PermissionStatus.DENIED -> Res.string.location_permission_denied_title
        else -> Res.string.location_permission_title
    }

val PermissionStatus.descriptionRes: StringResource
    get() = when (this) {
        PermissionStatus.DENIED_ALWAYS -> Res.string.location_permission_denied_always_description
        PermissionStatus.LOW_ACCURACY -> Res.string.location_permission_low_accuracy_description
        PermissionStatus.DENIED -> Res.string.location_permission_denied_description
        else -> Res.string.location_permission_description
    }

val PermissionStatus.buttonTextRes: StringResource
    get() = if (this == PermissionStatus.DENIED_ALWAYS) {
        Res.string.location_permission_settings_button
    } else {
        Res.string.location_permission_button
    }
