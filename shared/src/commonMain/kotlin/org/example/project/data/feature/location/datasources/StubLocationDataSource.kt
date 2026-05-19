package org.example.project.data.feature.location.datasources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.utils.models.Outcome

class StubLocationDataSource : LocationDataSource {
    override suspend fun getCurrentLocation(): Outcome<LocationCoordinates, LocationError> {
        return Outcome.Error(LocationError.SERVICE_UNAVAILABLE)
    }

    override fun observeLocationUpdates(): Flow<Outcome<LocationCoordinates, LocationError>> = flow {
        emit(Outcome.Error(LocationError.SERVICE_UNAVAILABLE))
    }

    override fun startBackgroundTracking(): Outcome<Unit, LocationError> {
        return Outcome.Error(LocationError.SERVICE_UNAVAILABLE)
    }

    override fun stopBackgroundTracking() {}
}
