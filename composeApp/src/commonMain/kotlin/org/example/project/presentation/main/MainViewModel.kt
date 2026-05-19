package org.example.project.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.feature.auth.usecases.LogoutUseCase
import org.example.project.domain.feature.auth.usecases.ObserveAuthStateUseCase
import org.example.project.domain.feature.location.usecases.ObserveLocationUpdatesUseCase
import org.example.project.domain.feature.location.usecases.StartBackgroundLocationTrackingUseCase
import org.example.project.domain.feature.location.usecases.StopBackgroundLocationTrackingUseCase
import org.example.project.utils.models.Outcome

class MainViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val observeLocationUpdatesUseCase: ObserveLocationUpdatesUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val startBackgroundLocationTrackingUseCase: StartBackgroundLocationTrackingUseCase,
    private val stopBackgroundLocationTrackingUseCase: StopBackgroundLocationTrackingUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                observeAuthStateUseCase(),
                observeLocationUpdatesUseCase(),
            ) { isAuthorized, locationOutcome ->
                if (!isAuthorized) {
                    _events.emit(MainEvent.LogoutSuccess)

                    _state.update {
                        MainScreenState(isAuthorized = false)
                    }
                } else {
                    _state.update { current ->
                        current.copy(
                            location = if (locationOutcome is Outcome.Success) {
                                Location.Precise(coordinates = locationOutcome.value)
                            } else {
                                current.location
                            },
                            locationError = (locationOutcome as? Outcome.Error)?.code,
                        )
                    }
                }
            }.collect {}
        }

        startBackgroundLocationTrackingUseCase()
    }

    fun showLogoutConfirmation() {
        _state.update {
            it.copy(sheetState = MainSheetState.LogoutConfirmation)
        }
    }

    fun hideLogoutConfirmation() {
        _state.update {
            it.copy(sheetState = MainSheetState.Hidden)
        }
    }

    fun logout() {
        _state.update {
            it.copy(sheetState = MainSheetState.Hidden)
        }

        viewModelScope.launch {
            logoutUseCase()
            _events.emit(MainEvent.LogoutSuccess)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopBackgroundLocationTrackingUseCase()
    }
}
