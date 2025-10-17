package com.faztbit.examen.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faztbit.examen.domain.engine.PointsEngine
import com.faztbit.examen.domain.usecase.GetMedalsUseCase
import com.faztbit.examen.domain.usecase.HandleAvatarTapUseCase
import com.faztbit.examen.domain.usecase.ResetAllProgressUseCase
import com.faztbit.examen.presentation.state.MedalsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedalsViewModel @Inject constructor(
    private val getMedalsUseCase: GetMedalsUseCase,
    private val handleAvatarTapUseCase: HandleAvatarTapUseCase,
    private val resetAllProgressUseCase: ResetAllProgressUseCase,
    private val pointsEngine: PointsEngine
) : ViewModel() {

    private val _uiState: MutableStateFlow<MedalsUiState> = MutableStateFlow(MedalsUiState())
    val uiState: StateFlow<MedalsUiState> = _uiState.asStateFlow()

    init {
        observeMedals()
        observeEngineState()
        observeLevelUpEvents()
    }

    private fun observeMedals() {
        viewModelScope.launch {
            getMedalsUseCase()
                .catch { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
                .collect { medals ->
                    _uiState.update { 
                        it.copy(
                            medals = medals,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun observeEngineState() {
        viewModelScope.launch {
            pointsEngine.isRunning.collect { isRunning ->
                _uiState.update { it.copy(isEngineRunning = isRunning) }
            }
        }
    }

    private fun observeLevelUpEvents() {
        viewModelScope.launch {
            pointsEngine.lastLevelUpMedal.collect { levelUpMedal ->
                _uiState.update { 
                    it.copy(
                        lastLevelUpMedal = levelUpMedal,
                        showLevelUpAnimation = levelUpMedal != null
                    )
                }
            }
        }
    }

    fun startEngine() {
        viewModelScope.launch {
            try {
                pointsEngine.startEngine()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun stopEngine() {
        viewModelScope.launch {
            try {
                pointsEngine.stopEngine()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    fun resumeEngine() {
        viewModelScope.launch {
            try {
                pointsEngine.resumeEngine()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onAvatarTap() {
        viewModelScope.launch {
            try {
                val shouldReset = handleAvatarTapUseCase.invoke()
                if (shouldReset) {
                    _uiState.update { it.copy(showResetConfirmation = true) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun confirmReset() {
        viewModelScope.launch {
            try {
                resetAllProgressUseCase.invoke()
                pointsEngine.stopEngine()
                _uiState.update { 
                    it.copy(
                        showResetConfirmation = false,
                        avatarTapCount = 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun dismissResetConfirmation() {
        _uiState.update { it.copy(showResetConfirmation = false) }
    }

    fun dismissLevelUpAnimation() {
        pointsEngine.clearLevelUpNotification()
        _uiState.update { 
            it.copy(
                showLevelUpAnimation = false,
                lastLevelUpMedal = null
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            pointsEngine.pauseEngine()
        }
    }
}