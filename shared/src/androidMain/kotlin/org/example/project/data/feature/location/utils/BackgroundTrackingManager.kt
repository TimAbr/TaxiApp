package org.example.project.data.feature.location.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import org.example.project.data.feature.location.service.LocationService
import org.example.project.domain.feature.location.models.PermissionStatus
import androidx.core.content.edit

class BackgroundTrackingManager(private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isTrackingEnabled: Boolean
        get() = prefs.getBoolean(KEY_TRACKING_ENABLED, false)
        private set(value) = prefs.edit { putBoolean(KEY_TRACKING_ENABLED, value) }

    init {
        sync()
    }

    fun start() {
        isTrackingEnabled = true
        sync()
    }

    fun stop() {
        isTrackingEnabled = false
        sync()
    }

    fun sync() {
        val hasPermission = context.checkLocationPermissionStatus() == PermissionStatus.GRANTED
        
        if (isTrackingEnabled && hasPermission) {
            val intent = Intent(context, LocationService::class.java)
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start service from background", e)
            }
        } else if (!isTrackingEnabled) {
            val intent = Intent(context, LocationService::class.java)
            context.stopService(intent)
        }
    }

    companion object {
        private const val TAG = "BackgroundTrackingMgr"
        private const val PREFS_NAME = "location_tracking_prefs"
        private const val KEY_TRACKING_ENABLED = "background_tracking_enabled"
    }
}
