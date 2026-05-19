package org.example.project.data.feature.location.providers

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import org.example.project.data.feature.location.utils.checkLocationPermissionStatus
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.providers.PermissionStatusProvider

class AndroidPermissionStatusProvider(
    private val context: Context
) : PermissionStatusProvider, DefaultLifecycleObserver {

    private val _permissionStatusFlow = MutableSharedFlow<PermissionStatus>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val permissionStatusFlow: Flow<PermissionStatus> = _permissionStatusFlow
        .onStart {
            updateStatus()
        }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        updateStatus()
    }

    override fun updateStatus() {
        val currentStatus = context.checkLocationPermissionStatus()
        _permissionStatusFlow.tryEmit(currentStatus)
    }
}
