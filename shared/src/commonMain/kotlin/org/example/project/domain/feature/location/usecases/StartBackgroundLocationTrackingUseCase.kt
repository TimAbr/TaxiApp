package org.example.project.domain.feature.location.usecases

import org.example.project.domain.feature.location.repository.LocationError
import org.example.project.domain.feature.location.repository.LocationRepository
import org.example.project.utils.models.Outcome

class StartBackgroundLocationTrackingUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Outcome<Unit, LocationError> {
        return locationRepository.startBackgroundTracking()
    }
}
