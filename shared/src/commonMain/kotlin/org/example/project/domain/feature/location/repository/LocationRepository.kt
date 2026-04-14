package org.example.project.domain.feature.location.repository

import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.utils.models.Outcome

interface LocationRepository {
    suspend fun getCurrentLocation(): Outcome<LocationCoordinates, LocationError>
}

enum class LocationError {
    NO_PERMISSION,
    GPS_DISABLED,
    TIMEOUT,
    SERVICE_UNAVAILABLE,
    UNKNOWN
}

