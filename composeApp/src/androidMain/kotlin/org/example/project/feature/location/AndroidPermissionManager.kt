package org.example.project.feature.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.providers.PermissionManager
import kotlin.coroutines.resume

class AndroidPermissionManager : PermissionManager {

    private var _activity: ComponentActivity? = null
    private val activity: ComponentActivity get() = _activity!!

    private var _permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permissionLauncher: ActivityResultLauncher<Array<String>> get() = _permissionLauncher!!

    private var currentContinuation: CancellableContinuation<PermissionStatus>? = null

    override suspend fun checkLocationPermission(): PermissionStatus {
        val fineGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return when {
            fineGranted -> PermissionStatus.GRANTED
            coarseGranted -> PermissionStatus.LOW_ACCURACY
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> PermissionStatus.DENIED
            else -> PermissionStatus.NOT_DETERMINED
        }
    }

    override fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
    }

    override suspend fun requestLocationPermission(): PermissionStatus {
        val currentStatus = checkLocationPermission()
        if (currentStatus == PermissionStatus.GRANTED) return PermissionStatus.GRANTED

        return suspendCancellableCoroutine { continuation ->
            currentContinuation = continuation
            continuation.invokeOnCancellation { currentContinuation = null }

            try {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } catch (e: Exception) {
                continuation.resume(PermissionStatus.CANCELED)
                currentContinuation = null
            }
        }
    }

    fun bind(activity: ComponentActivity) {
        _activity = activity
        _permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val fineGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            val status = when {
                fineGranted -> PermissionStatus.GRANTED
                coarseGranted -> PermissionStatus.LOW_ACCURACY
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> PermissionStatus.DENIED
                else -> PermissionStatus.DENIED_ALWAYS
            }

            currentContinuation?.resume(status)
            currentContinuation = null
        }
    }

    fun unbind() {
        currentContinuation?.resume(PermissionStatus.CANCELED)
        currentContinuation = null
        _activity = null
        _permissionLauncher = null
    }
}