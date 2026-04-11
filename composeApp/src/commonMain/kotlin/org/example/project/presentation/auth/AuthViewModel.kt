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

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(isAuthorized = getAuthStateUseCase())
        }
        viewModelScope.launch {
            observeAuthStateUseCase().collect { isAuthorized ->
                _state.update {
                    it.copy(isAuthorized = isAuthorized)
                }
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                )
            }
            val outcome = loginWithGoogleUseCase()
            when (outcome) {
                is Outcome.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthorized = true,
                        )
                    }
                }
                is Outcome.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = outcome.code,
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _state.update {
            it.copy(error = null)
        }
    }
}
