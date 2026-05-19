package org.example.project.domain.feature.location.providers

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.feature.location.models.PermissionStatus

interface PermissionStatusProvider {
    val permissionStatusFlow: Flow<PermissionStatus>
    fun updateStatus()
}
