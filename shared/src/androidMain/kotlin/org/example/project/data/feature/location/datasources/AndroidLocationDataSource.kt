package org.example.project.data.feature.location.datasources

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.example.project.data.feature.location.utils.BackgroundTrackingManager
import org.example.project.data.feature.location.utils.checkLocationPermissionStatus
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.models.PermissionStatus
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.utils.models.Outcome
import kotlin.coroutines.resume

class AndroidLocationDataSource(
    private val context: Context,
    private val trackingManager: BackgroundTrackingManager,
) : LocationDataSource {

    private val dataSourceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val sharedLocationFlow = createLocationFlow()
        .shareIn(
            scope = dataSourceScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            replay = 1
        )

    override fun observeLocationUpdates(): Flow<Outcome<LocationCoordinates, LocationError>> {
        return sharedLocationFlow
    }

    override fun startBackgroundTracking(): Outcome<Unit, LocationError> {
        trackingManager.start()

        val status = context.checkLocationPermissionStatus()
        return if (status == PermissionStatus.GRANTED) {
            Outcome.Success(Unit)
        } else {
            Outcome.Error(LocationError.NO_PERMISSION)
        }
    }

    override fun stopBackgroundTracking() {
        trackingManager.stop()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createLocationFlow(): Flow<Outcome<LocationCoordinates, LocationError>> {
        return providerStatusFlow()
            .map { getStatusError() }
            .distinctUntilChanged()
            .flatMapLatest { error ->
                if (error != null) {
                    flowOf(Outcome.Error(error))
                } else {
                    rawLocationFlow()
                        .map<LocationCoordinates, Outcome<LocationCoordinates, LocationError>> { 
                            Outcome.Success(it) 
                        }
                        .catch { e ->
                            if (e is SecurityException) {
                                emit(Outcome.Error(LocationError.NO_PERMISSION))
                            } else {
                                throw e
                            }
                        }
                        .withTimeoutCheck(
                            timeoutMillis = REQUEST_TIMEOUT,
                            error = LocationError.TIMEOUT
                        )
                }
            }
            .distinctUntilChanged()
    }

    private fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Outcome<LocationCoordinates, LocationError> {
        if (!isLocationPermissionGranted()) return Outcome.Error(LocationError.NO_PERMISSION)
        if (!isGpsEnabled()) return Outcome.Error(LocationError.GPS_DISABLED)

        return try {
            withTimeout(REQUEST_TIMEOUT) {
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (continuation.isActive) {
                                if (location != null) {
                                    continuation.resume(Outcome.Success(location.toCoordinates()))
                                } else {
                                    requestSingleUpdate(continuation)
                                }
                            }
                        }
                        .addOnFailureListener {
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
        continuation: CancellableContinuation<Outcome<LocationCoordinates, LocationError>>,
    ) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, REQUEST_INTERVAL)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                if (continuation.isActive) {
                    if (loc != null) {
                        continuation.resume(Outcome.Success(loc.toCoordinates()))
                    } else {
                        continuation.resume(Outcome.Error(LocationError.SERVICE_UNAVAILABLE))
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        continuation.invokeOnCancellation { fusedLocationClient.removeLocationUpdates(callback) }
    }

    private fun providerStatusFlow(): Flow<Unit> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(Unit)
            }
        }
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        
        trySend(Unit)
        awaitClose { 
            context.unregisterReceiver(receiver)
        }
    }

    @SuppressLint("MissingPermission")
    private fun rawLocationFlow(): Flow<LocationCoordinates> = callbackFlow {
        if (!isLocationPermissionGranted()) {
            throw SecurityException("Location permission missing")
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, REQUEST_INTERVAL)
            .setMinUpdateIntervalMillis(MIN_REQUEST_INTERVAL)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.toCoordinates()) }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            .addOnFailureListener { e -> close(e) }

        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    private fun <T> Flow<Outcome<T, LocationError>>.withTimeoutCheck(
        timeoutMillis: Long,
        error: LocationError,
    ): Flow<Outcome<T, LocationError>> = callbackFlow {
        var timeoutJob: Job? = null

        fun startTimeoutTimer() {
            timeoutJob?.cancel()
            timeoutJob = launch {
                delay(timeoutMillis)
                trySend(Outcome.Error(error))
            }
        }

        val collectionJob = launch {
            collect { value ->
                trySend(value)
                if (value is Outcome.Success) startTimeoutTimer()
            }
        }

        startTimeoutTimer()
        awaitClose {
            timeoutJob?.cancel()
            collectionJob.cancel()
        }
    }

    private fun getStatusError(): LocationError? {
        val gmsAvailability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        return when {
            !isLocationPermissionGranted() -> LocationError.NO_PERMISSION
            !isGpsEnabled() -> LocationError.GPS_DISABLED
            gmsAvailability != ConnectionResult.SUCCESS -> LocationError.SERVICE_UNAVAILABLE
            else -> null
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return context.checkLocationPermissionStatus() == PermissionStatus.GRANTED
    }

    private fun Location.toCoordinates() = LocationCoordinates(lat = latitude, lon = longitude)

    companion object {
        private const val REQUEST_TIMEOUT = 10000L
        private const val REQUEST_INTERVAL = 3000L
        private const val MIN_REQUEST_INTERVAL = 1000L
    }
}
