package org.example.project.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.example.project.domain.feature.auth.usecases.LogoutUseCase

class MainViewModel(
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _events = MutableSharedFlow<MainEvent>()
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _events.emit(MainEvent.LogoutSuccess)
        }
    }

    sealed interface MainEvent {
        object LogoutSuccess : MainEvent
    }
}
