package org.example.project.data.feature.location.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.example.project.domain.feature.location.models.PermissionStatus

fun Context.checkLocationPermissionStatus(): PermissionStatus {
    val fineGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return when {
        fineGranted -> PermissionStatus.GRANTED
        coarseGranted -> PermissionStatus.LOW_ACCURACY
        this is Activity && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> PermissionStatus.DENIED
        else -> PermissionStatus.NOT_DETERMINED
    }
}
