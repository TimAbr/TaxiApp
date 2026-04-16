package org.example.project.data.feature.location.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.data.feature.location.datasources.LocationDataSource
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.domain.feature.location.repository.LocationRepository
import org.example.project.utils.models.Outcome

class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource
) : LocationRepository {
    override suspend fun getCurrentLocation(
    ): Outcome<LocationCoordinates, LocationError> {
        return locationDataSource.getCurrentLocation()
    }

    override fun observeLocationUpdates(
    ): Flow<Outcome<LocationCoordinates, LocationError>> {
        return locationDataSource.observeLocationUpdates()
    }
}
