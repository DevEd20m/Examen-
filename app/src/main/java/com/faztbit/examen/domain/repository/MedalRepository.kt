package com.faztbit.examen.domain.repository

import com.faztbit.examen.domain.entity.Medal
import kotlinx.coroutines.flow.Flow

interface MedalRepository {
    suspend fun getMedals(): Flow<List<Medal>>
    suspend fun updateMedal(medal: Medal)
    suspend fun resetAllMedals()
    suspend fun initializeDefaultMedals()
    suspend fun getAvatarTapCount(): Int
    suspend fun updateAvatarTapCount(count: Int)
    suspend fun getLastTapTime(): Long
    suspend fun updateLastTapTime(time: Long)
    suspend fun getEngineState(): Boolean
    suspend fun setEngineState(isRunning: Boolean)
}