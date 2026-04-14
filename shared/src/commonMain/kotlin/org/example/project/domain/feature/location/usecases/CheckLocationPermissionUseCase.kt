package org.example.project.domain.feature.location.usecases

import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.providers.PermissionManager

class CheckLocationPermissionUseCase(
    private val permissionManager: PermissionManager,
) {
    operator fun invoke(): PermissionStatus {
        return permissionManager.checkLocationPermission()
    }
}
