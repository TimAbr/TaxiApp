package org.example.project.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.feature.auth.usecases.GetAuthStateUseCase
import org.example.project.domain.feature.auth.usecases.LoginWithGoogleUseCase
import org.example.project.domain.feature.auth.usecases.ObserveAuthStateUseCase
import org.example.project.utils.models.Outcome

class AuthViewModel(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthScreenState>(
        if (getAuthStateUseCase()) {
            AuthScreenState.Authorized
        } else {
            AuthScreenState.LogIn
        },
    )
    val state: StateFlow<AuthScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { isAuthorized ->
                if (isAuthorized) {
                    _state.value = AuthScreenState.Authorized
                }
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _state.value = AuthScreenState.Loading
            val outcome = loginWithGoogleUseCase()
            _state.value = when (outcome) {
                is Outcome.Success -> AuthScreenState.Authorized
                is Outcome.Error -> AuthScreenState.Error(outcome.code)
            }
        }
    }

    fun clearError() {
        _state.update { currentState ->
            if (currentState is AuthScreenState.Error) {
                AuthScreenState.LogIn
            } else {
                currentState
            }
        }
    }
}
