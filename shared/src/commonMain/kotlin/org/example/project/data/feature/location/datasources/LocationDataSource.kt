package org.example.project.data.feature.location.datasources

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.utils.models.Outcome

interface LocationDataSource {
    suspend fun getCurrentLocation(): Outcome<LocationCoordinates, LocationError>
    fun observeLocationUpdates(): Flow<Outcome<LocationCoordinates, LocationError>>
}
