package org.example.project.data.feature.location.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.project.data.feature.location.datasources.AndroidLocationDataSource
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.shared.R
import org.example.project.utils.models.Outcome
import org.koin.android.ext.android.inject

class LocationService : Service() {

    private val dataSource: AndroidLocationDataSource by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var trackingJob: Job? = null

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val notification = createNotification(null)
        
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )
        
        if (trackingJob == null) {
            trackingJob = dataSource.observeLocationUpdates()
                .onEach { outcome ->
                    if (outcome is Outcome.Success) {
                        updateNotification(outcome.value)
                    }
                }
                .launchIn(serviceScope)
        }
    }

    private fun updateNotification(coordinates: LocationCoordinates) {
        val notification = createNotification(coordinates)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(coordinates: LocationCoordinates?): Notification {
        val contentText = if (coordinates != null) {
            getString(R.string.location_notification_content, coordinates.lat, coordinates.lon)
        } else {
            getString(R.string.location_notification_text)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.location_notification_title))
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.location_tracking_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        trackingJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val NOTIFICATION_ID = 1
    }
}
