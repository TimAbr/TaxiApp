package org.example.project.domain.feature.location.providers

import org.example.project.domain.feature.location.models.PermissionStatus

interface PermissionManager {
    suspend fun requestLocationPermission(): PermissionStatus
    suspend fun checkLocationPermission(): PermissionStatus
}