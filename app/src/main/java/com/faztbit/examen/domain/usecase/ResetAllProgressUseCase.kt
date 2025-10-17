package com.faztbit.examen.domain.usecase

import com.faztbit.examen.domain.repository.MedalRepository
import javax.inject.Inject

interface ResetAllProgressUseCase {
    suspend operator fun invoke()
}

class ResetAllProgressUseCaseImpl @Inject constructor(
    private val repository: MedalRepository
) : ResetAllProgressUseCase {
    override suspend fun invoke() {
        repository.resetAllMedals()
        repository.updateAvatarTapCount(0)
        repository.updateLastTapTime(0L)
    }
}