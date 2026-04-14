package org.example.project.presentation.location.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.usecases.CheckLocationPermissionUseCase
import org.example.project.domain.feature.location.usecases.RequestLocationPermissionUseCase

class LocationPermissionViewModel(
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase,
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase,
) : ViewModel() {

    private val _permissionStatus = MutableStateFlow(PermissionStatus.DENIED)
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus.asStateFlow()

    init {
        checkPermission()
    }

    private fun checkPermission() {
        viewModelScope.launch {
            _permissionStatus.value = checkLocationPermissionUseCase()
        }
    }

    fun requestPermission() {
        viewModelScope.launch {
            _permissionStatus.value = requestLocationPermissionUseCase()
        }
    }
}
