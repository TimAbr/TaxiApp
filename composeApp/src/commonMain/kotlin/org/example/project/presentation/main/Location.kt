package org.example.project.presentation.main

import org.example.project.domain.feature.location.models.LocationCoordinates

sealed interface Location {
    object NotDefined : Location

    data class Precise(
        val coordinates: LocationCoordinates,
    ) : Location
}
