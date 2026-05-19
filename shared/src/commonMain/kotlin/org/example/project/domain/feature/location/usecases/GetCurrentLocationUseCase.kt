package org.example.project.domain.feature.location.usecases

import org.example.project.domain.feature.location.models.LocationCoordinates
import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.domain.feature.location.repository.LocationRepository
import org.example.project.utils.models.Outcome

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(): Outcome<LocationCoordinates, LocationError> {
        return locationRepository.getCurrentLocation()
    }
}
