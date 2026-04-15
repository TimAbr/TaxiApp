package org.example.project.feature.location

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.location.LOCATION
import platform.CoreLocation.CLLocationManager
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.providers.PermissionManager
import platform.CoreLocation.CLAccuracyAuthorization

class IosPermissionManager(
    private val permissionsController: PermissionsController,
) : PermissionManager {

    override suspend fun requestLocationPermission(): PermissionStatus {
        return try {
            permissionsController.providePermission(Permission.LOCATION)
            checkLocationPermission()
        } catch (_: Exception) {
            checkLocationPermission()
        }
    }

    override suspend fun checkLocationPermission(): PermissionStatus {
        return permissionsController.getPermissionState(Permission.LOCATION)
            .toPermissionStatus()
    }

    override fun openSettings() {
        permissionsController.openAppSettings()
    }

    private fun PermissionState.toPermissionStatus(): PermissionStatus {
        return when (this) {
            PermissionState.Granted -> {
                val accuracy = CLLocationManager().accuracyAuthorization

                if (accuracy == CLAccuracyAuthorization.CLAccuracyAuthorizationFullAccuracy) {
                    PermissionStatus.GRANTED
                } else {
                    PermissionStatus.LOW_ACCURACY
                }
            }

            PermissionState.DeniedAlways -> PermissionStatus.DENIED_ALWAYS

            PermissionState.NotDetermined -> PermissionStatus.NOT_DETERMINED

            PermissionState.Denied,
            PermissionState.NotGranted -> PermissionStatus.DENIED
        }
    }
}