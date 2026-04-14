package org.example.project.domain.feature.location.usecases

import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.providers.PermissionManager

class RequestLocationPermissionUseCase(
    private val permissionManager: PermissionManager,
) {
    suspend operator fun invoke(): PermissionStatus {
        return permissionManager.requestLocationPermission()
    }
}
