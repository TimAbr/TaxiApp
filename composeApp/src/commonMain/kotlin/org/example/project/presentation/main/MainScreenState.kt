package org.example.project.presentation.main

import org.example.project.domain.feature.location.repository.LocationError

data class MainScreenState(
    val location: Location = Location.NotDefined,
    val locationError: LocationError? = null,
    val isAuthorized: Boolean = true,
    val sheetState: MainSheetState = MainSheetState.Hidden,
)
