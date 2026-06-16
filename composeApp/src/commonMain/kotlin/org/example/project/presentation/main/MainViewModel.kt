package org.example.project.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.feature.auth.usecases.LogoutUseCase

class MainViewModel(
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    fun showLogoutConfirmation() {
        _state.update { it.copy(sheetState = MainSheetState.LogoutConfirmation) }
    }

    fun hideLogoutConfirmation() {
        _state.update { it.copy(sheetState = MainSheetState.Hidden) }
    }

    fun logout() {
        _state.update { it.copy(sheetState = MainSheetState.Hidden) }
        viewModelScope.launch {
            logoutUseCase()
            _events.emit(MainEvent.LogoutSuccess)
        }
    }
}
