package com.faztbit.examen.domain.usecase

import com.faztbit.examen.domain.repository.MedalRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface HandleAvatarTapUseCase {
    suspend operator fun invoke(): Boolean // Returns true if should reset
}

class HandleAvatarTapUseCaseImpl @Inject constructor(
    private val repository: MedalRepository,
    private val resetAllProgressUseCase: ResetAllProgressUseCase
) : HandleAvatarTapUseCase {
    
    companion object {
        private const val TAP_RESET_TIMEOUT_MS = 3000L
        private const val RESET_TAP_THRESHOLD = 5
    }
    
    override suspend fun invoke(): Boolean {
        val currentTime: Long = System.currentTimeMillis()
        val lastTapTime: Long = repository.getLastTapTime()
        val currentTapCount: Int = repository.getAvatarTapCount()
        
        val shouldResetCount: Boolean = (currentTime - lastTapTime) > TAP_RESET_TIMEOUT_MS
        val newTapCount: Int = if (shouldResetCount) 1 else currentTapCount + 1
        
        repository.updateAvatarTapCount(newTapCount)
        repository.updateLastTapTime(currentTime)
        
        if (newTapCount >= RESET_TAP_THRESHOLD) {
            resetAllProgressUseCase.invoke()
            return true
        }
        
        return false
    }
}