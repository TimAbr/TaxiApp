package org.example.project.data.feature.location.datasources

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.utils.models.Outcome
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidLocationDataSource(
    private val context: Context
) : LocationDataSource {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Outcome<LocationCoordinates, LocationError> {
        if (!hasPermission())
            return Outcome.Error(
                LocationError.NO_PERMISSION
            )

        if (!isGpsEnabled()) {
            return Outcome.Error(LocationError.GPS_DISABLED)
        }

        return try {
            withTimeout(REQUEST_TIMEOUT) {
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            if (location != null) {
                                continuation.resume(
                                    Outcome.Success(
                                        LocationCoordinates(
                                            location.latitude, location.longitude
                                        )
                                    )
                                )
                            } else {
                                requestSingleUpdate(continuation)
                            }
                        }
                    }.addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(Outcome.Error(LocationError.UNKNOWN))
                        }
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Outcome.Error(LocationError.TIMEOUT)
        } catch (e: Exception) {
            Outcome.Error(LocationError.UNKNOWN)
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestSingleUpdate(
        continuation: CancellableContinuation<Outcome<LocationCoordinates, LocationError>>
    ) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            REQUEST_INTERVAL
        )
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                if (continuation.isActive) {
                    if (loc != null) {
                        continuation.resume(
                            Outcome.Success(loc.toCoordinates())
                        )
                    } else {
                        continuation.resume(
                            Outcome.Error(LocationError.SERVICE_UNAVAILABLE)
                        )
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )

        continuation.invokeOnCancellation {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    @SuppressLint("MissingPermission")
    override fun observeLocationUpdates(): Flow<Outcome<LocationCoordinates, LocationError>> =
        callbackFlow {

            var timeoutJob: Job? = null
            var isRequestingUpdates = false

            fun startTimeoutTimer() {
                timeoutJob?.cancel()
                timeoutJob = launch {
                    delay(REQUEST_TIMEOUT)
                    trySend(Outcome.Error(LocationError.TIMEOUT))
                }
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        trySend(
                            Outcome.Success(
                                LocationCoordinates(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        )
                        startTimeoutTimer()
                    }
                }
            }

            fun stopLocationUpdates() {
                if (isRequestingUpdates) {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    isRequestingUpdates = false
                }
                timeoutJob?.cancel()
            }


            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                REQUEST_INTERVAL
            )
                .setMinUpdateIntervalMillis(MIN_REQUEST_INTERVAL)
                .build()


            fun manageUpdatesState() {
                val error = getStatusError()
                if (error == null) {
                    if (!isRequestingUpdates) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                        isRequestingUpdates = true
                        startTimeoutTimer()
                    }
                } else {
                    stopLocationUpdates()
                    trySend(Outcome.Error(error))
                }
            }

            val gpsStatusReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    manageUpdatesState()
                }
            }
            ContextCompat.registerReceiver(
                context,
                gpsStatusReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )


            manageUpdatesState()

            awaitClose {
                timeoutJob?.cancel()
                fusedLocationClient.removeLocationUpdates(locationCallback)
                context.unregisterReceiver(gpsStatusReceiver)
            }
        }

    private fun getStatusError(): LocationError? {
        val gmsAvailability = GoogleApiAvailability
            .getInstance()
            .isGooglePlayServicesAvailable(context)

        return when {
            !hasPermission() -> LocationError.NO_PERMISSION
            !isGpsEnabled() -> LocationError.GPS_DISABLED
            gmsAvailability != ConnectionResult.SUCCESS -> LocationError.SERVICE_UNAVAILABLE
            else -> null
        }
    }

    private fun Location.toCoordinates() = LocationCoordinates(latitude, longitude)

    companion object{
        private const val REQUEST_TIMEOUT = 10000L
        private const val REQUEST_INTERVAL = 3000L
        private const val MIN_REQUEST_INTERVAL = 1000L
    }
}
