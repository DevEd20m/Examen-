package com.faztbit.examen.domain.usecase

import com.faztbit.examen.domain.entity.Medal
import com.faztbit.examen.domain.repository.MedalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetMedalsUseCase {
    suspend operator fun invoke(): Flow<List<Medal>>
}

class GetMedalsUseCaseImpl @Inject constructor(
    private val repository: MedalRepository
) : GetMedalsUseCase {
    override suspend fun invoke(): Flow<List<Medal>> {
        return repository.getMedals()
    }
}