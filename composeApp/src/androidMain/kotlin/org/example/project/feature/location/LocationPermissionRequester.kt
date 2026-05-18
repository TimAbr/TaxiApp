package org.example.project.feature.location

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

class LocationPermissionRequester {

    private var launcher: ActivityResultLauncher<Array<String>>? = null
    private var currentContinuation: CancellableContinuation<Map<String, Boolean>>? = null
    private val mutex = Mutex()

    fun onBind(activity: ComponentActivity) {
        launcher = activity.activityResultRegistry.register(
            PERMISSION_REQUEST_KEY,
            activity,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            currentContinuation?.resume(result)
            currentContinuation = null
        }
    }

    suspend fun request(): Map<String, Boolean> = mutex.withLock {
        val lch = launcher ?: return emptyMap()

        return suspendCancellableCoroutine { continuation ->
            currentContinuation = continuation
            continuation.invokeOnCancellation {
                if (currentContinuation === continuation) currentContinuation = null
            }

            try {
                lch.launch(PERMISSIONS)
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(emptyMap())
                currentContinuation = null
            }
        }
    }

    fun onUnbind(isChangingConfig: Boolean) {
        if (!isChangingConfig) {
            currentContinuation?.resume(emptyMap())
            currentContinuation = null
        }
        launcher?.unregister()
        launcher = null
    }

    companion object {
        const val PERMISSION_REQUEST_KEY = "location_permission_request"
        
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
