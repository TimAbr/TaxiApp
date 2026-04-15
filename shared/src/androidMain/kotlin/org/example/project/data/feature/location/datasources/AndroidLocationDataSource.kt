package org.example.project.data.feature.location.datasources

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.utils.models.Outcome
import kotlin.coroutines.resume

class AndroidLocationDataSource(
    private val context: Context
) : LocationDataSource {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Outcome<LocationCoordinates, LocationError> {
        if (!isGpsEnabled()) {
            return Outcome.Error(LocationError.GPS_DISABLED)
        }

        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(Outcome.Success(LocationCoordinates(location.latitude, location.longitude)))
                } else {
                    // If last location is null, try to get fresh location
                    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                        .setMaxUpdates(1)
                        .build()

                    val callback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            val lastLocation = result.lastLocation
                            if (lastLocation != null) {
                                if (continuation.isActive) {
                                    continuation.resume(Outcome.Success(LocationCoordinates(lastLocation.latitude, lastLocation.longitude)))
                                }
                            } else {
                                if (continuation.isActive) {
                                    continuation.resume(Outcome.Error(LocationError.UNKNOWN))
                                }
                            }
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, callback, null)
                }
            }.addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume(Outcome.Error(LocationError.UNKNOWN))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun observeLocationUpdates(): Flow<Outcome<LocationCoordinates, LocationError>> = callbackFlow {
        if (!isGpsEnabled()) {
            trySend(Outcome.Error(LocationError.GPS_DISABLED))
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(Outcome.Success(LocationCoordinates(location.latitude, location.longitude)))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
