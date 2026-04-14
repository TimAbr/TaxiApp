package org.example.project.domain.feature.location.usecases

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.domain.feature.location.repository.LocationRepository
import org.example.project.utils.models.Outcome

class ObserveLocationUpdatesUseCase(
    private val locationRepository: LocationRepository,
) {
    operator fun invoke(): Flow<Outcome<LocationCoordinates, LocationError>> {
        return locationRepository.observeLocationUpdates()
    }
}
