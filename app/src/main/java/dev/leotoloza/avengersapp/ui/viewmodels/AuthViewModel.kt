package dev.leotoloza.avengersapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.leotoloza.avengersapp.domain.usecase.GetAuthUiConfigUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthUiConfigUseCase: GetAuthUiConfigUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthUiConfig()
    }

    private fun checkAuthUiConfig() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val shouldUseFirebaseUi = getAuthUiConfigUseCase()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                shouldUseFirebaseUi = shouldUseFirebaseUi
            )
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = true,
    val shouldUseFirebaseUi: Boolean = false
)
