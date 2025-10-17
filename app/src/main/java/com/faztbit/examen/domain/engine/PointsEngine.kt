package com.faztbit.examen.domain.engine

import com.faztbit.examen.domain.entity.Medal
import com.faztbit.examen.domain.repository.MedalRepository
import com.faztbit.examen.domain.usecase.GetMedalsUseCase
import com.faztbit.examen.domain.usecase.UpdateMedalPointsUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PointsEngine @Inject constructor(
    private val medalRepository: MedalRepository,
    private val getMedalsUseCase: GetMedalsUseCase,
    private val updateMedalPointsUseCase: UpdateMedalPointsUseCase
) {
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _lastLevelUpMedal = MutableStateFlow<Medal?>(null)
    val lastLevelUpMedal: StateFlow<Medal?> = _lastLevelUpMedal.asStateFlow()

    private var engineJob: Job? = null
    private val engineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val POINT_GENERATION_INTERVAL_MS = 2000L // 2 seconds
        private const val MIN_POINTS_PER_CYCLE = 1
        private const val MAX_POINTS_PER_CYCLE = 5
    }

    suspend fun startEngine() {
        if (_isRunning.value) return

        _isRunning.value = true
        medalRepository.setEngineState(true)

        engineJob = engineScope.launch {
            while (isActive && _isRunning.value) {
                try {
                    generateRandomPoints()
                    delay(POINT_GENERATION_INTERVAL_MS)
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {
                    // Log error but continue running
                    delay(POINT_GENERATION_INTERVAL_MS)
                }
            }
        }
    }

    suspend fun stopEngine() {
        _isRunning.value = false
        medalRepository.setEngineState(false)
        engineJob?.cancel()
        engineJob = null
    }

    fun pauseEngine() {
        _isRunning.value = false
        engineJob?.cancel()
        engineJob = null
    }

    suspend fun resumeEngine() {
        val savedEngineState: Boolean = medalRepository.getEngineState()
        if (savedEngineState) {
            startEngine()
        }
    }

    private suspend fun generateRandomPoints() {
        val medals: List<Medal> = getMedalsUseCase.invoke().first()
        val eligibleMedals: List<Medal> = medals.filter { !it.isMaxLevel }
        
        if (eligibleMedals.isEmpty()) {
            stopEngine()
            return
        }

        // Select random medal and points
        val randomMedal: Medal = eligibleMedals.random()
        val pointsToAdd: Int = Random.nextInt(MIN_POINTS_PER_CYCLE, MAX_POINTS_PER_CYCLE + 1)

        // Update medal points
        val oldLevel: Int = randomMedal.currentLevel
        updateMedalPointsUseCase.invoke(randomMedal.id, pointsToAdd)

        // Check if medal leveled up
        val updatedMedals: List<Medal> = getMedalsUseCase.invoke().first()
        val updatedMedal: Medal? = updatedMedals.find { it.id == randomMedal.id }
        
        if (updatedMedal != null && updatedMedal.currentLevel > oldLevel) {
            _lastLevelUpMedal.value = updatedMedal
            // Clear the level up notification after a delay
            delay(3000)
            _lastLevelUpMedal.value = null
        }
    }

    fun clearLevelUpNotification() {
        _lastLevelUpMedal.value = null
    }
}