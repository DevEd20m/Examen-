package com.faztbit.examen.presentation.state

import com.faztbit.examen.domain.entity.Medal

data class MedalsUiState(
    val medals: List<Medal> = emptyList(),
    val isLoading: Boolean = true,
    val isEngineRunning: Boolean = false,
    val lastLevelUpMedal: Medal? = null,
    val showLevelUpAnimation: Boolean = false,
    val avatarTapCount: Int = 0,
    val showResetConfirmation: Boolean = false,
    val error: String? = null
)