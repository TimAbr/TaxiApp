package org.example.project.presentation.location.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.providers.PermissionManager

class LocationPermissionViewModel(
    private val permissionManager: PermissionManager,
) : ViewModel() {

    private val _permissionStatus = MutableStateFlow(PermissionStatus.NOT_DETERMINED)
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus.asStateFlow()

    init {
        checkPermission()
    }

    private fun checkPermission() {
        viewModelScope.launch {
            _permissionStatus.value = permissionManager.checkLocationPermission()
        }
    }

    fun requestPermission() {
        viewModelScope.launch {
            if (permissionStatus.value== PermissionStatus.DENIED_ALWAYS) {
                permissionManager.openSettings()
            } else {
                _permissionStatus.value = permissionManager.requestLocationPermission()
            }
        }
    }

    fun openSettings() {
        permissionManager.openSettings()
    }
}
