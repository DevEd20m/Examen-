package com.faztbit.examen.domain.usecase

import com.faztbit.examen.domain.entity.Medal
import com.faztbit.examen.domain.repository.MedalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UpdateMedalPointsUseCase {
    suspend operator fun invoke(medalId: Int, points: Int): Medal?
}

class UpdateMedalPointsUseCaseImpl @Inject constructor(
    private val repository: MedalRepository
) : UpdateMedalPointsUseCase {
    override suspend fun invoke(medalId: Int, points: Int): Medal? {
        val medals: Flow<List<Medal>> = repository.getMedals()
        var updatedMedal: Medal? = null
        
        medals.collect { medalList ->
            val medal = medalList.find { it.id == medalId }
            medal?.let { currentMedal ->
                val medalWithPoints: Medal = currentMedal.addPoints(points)
                updatedMedal = if (medalWithPoints.currentPoints >= 100 && !medalWithPoints.isMaxLevel) {
                    medalWithPoints.levelUp()
                } else {
                    medalWithPoints
                }
                updatedMedal?.let { repository.updateMedal(it) }
            }
        }
        
        return updatedMedal
    }
}