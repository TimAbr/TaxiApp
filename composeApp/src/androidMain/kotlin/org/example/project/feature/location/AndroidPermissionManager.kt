package org.example.project.feature.location

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import org.example.project.data.feature.location.utils.checkLocationPermissionStatus
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.providers.PermissionManager

class AndroidPermissionManager(
    private val delegate: PermissionLifecycleDelegate,
    private val requester: LocationPermissionRequester
) : PermissionManager, LifecycleBinder by delegate {

    override suspend fun checkLocationPermission(): PermissionStatus {
        val activity = delegate.activity ?: return PermissionStatus.NOT_DETERMINED
        return activity.checkLocationPermissionStatus()
    }

    override fun openSettings() {
        delegate.activity?.let {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts(SCHEME_PACKAGE, it.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            it.startActivity(intent)
        }
    }

    override suspend fun requestLocationPermission(): PermissionStatus {
        val currentStatus = checkLocationPermission()
        if (currentStatus == PermissionStatus.GRANTED) return PermissionStatus.GRANTED

        val result = requester.request()

        val fineGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        val activity = delegate.activity ?: return PermissionStatus.CANCELED

        return when {
            fineGranted -> PermissionStatus.GRANTED
            coarseGranted -> PermissionStatus.LOW_ACCURACY
            else -> activity.checkLocationPermissionStatus().let { 
                if (it == PermissionStatus.DENIED) PermissionStatus.DENIED else PermissionStatus.DENIED_ALWAYS
            }
        }
    }

    companion object {
        private const val SCHEME_PACKAGE = "package"
    }
}
