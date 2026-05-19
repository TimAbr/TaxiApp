package org.example.project.domain.feature.location.usecases

import org.example.project.domain.feature.location.repository.LocationRepository

class StopBackgroundLocationTrackingUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke() {
        locationRepository.stopBackgroundTracking()
    }
}
