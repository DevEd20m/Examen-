package com.faztbit.examen.domain.engine

import com.faztbit.examen.domain.entity.Medal
import com.faztbit.examen.domain.repository.MedalRepository
import com.faztbit.examen.domain.usecase.GetMedalsUseCase
import com.faztbit.examen.domain.usecase.UpdateMedalPointsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PointsEngineTest {

    @Mock
    private lateinit var medalRepository: MedalRepository

    @Mock
    private lateinit var getMedalsUseCase: GetMedalsUseCase

    @Mock
    private lateinit var updateMedalPointsUseCase: UpdateMedalPointsUseCase

    private lateinit var pointsEngine: PointsEngine

    private val testMedals = listOf(
        Medal(1, "Medal 1", 1, 50, 5, "icon1"),
        Medal(2, "Medal 2", 2, 75, 5, "icon2"),
        Medal(3, "Medal 3", 5, 0, 5, "icon3") // Max level reached
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        pointsEngine = PointsEngine(medalRepository, getMedalsUseCase, updateMedalPointsUseCase)
    }

    @Test
    fun `engine starts with correct initial state`() = runTest {
        // When
        val isRunning = pointsEngine.isRunning.first()
        val lastLevelUp = pointsEngine.lastLevelUpMedal.first()

        // Then
        assertFalse(isRunning)
        assertEquals(null, lastLevelUp)
    }

    @Test
    fun `startEngine changes running state to true`() = runTest {
        // Given
        whenever(getMedalsUseCase.invoke()).thenReturn(flowOf(testMedals))
        whenever(medalRepository.getEngineState()).thenReturn(false)

        // When
        pointsEngine.startEngine()

        // Then
        assertTrue(pointsEngine.isRunning.first())
        verify(medalRepository).setEngineState(true)
    }

    @Test
    fun `stopEngine changes running state to false`() = runTest {
        // Given
        whenever(getMedalsUseCase.invoke()).thenReturn(flowOf(testMedals))
        whenever(medalRepository.getEngineState()).thenReturn(false)

        // When
        pointsEngine.startEngine()
        pointsEngine.stopEngine()

        // Then
        assertFalse(pointsEngine.isRunning.first())
        verify(medalRepository).setEngineState(false)
    }

    @Test
    fun `pauseEngine preserves state in repository`() = runTest {
        // Given
        whenever(getMedalsUseCase.invoke()).thenReturn(flowOf(testMedals))
        whenever(medalRepository.getEngineState()).thenReturn(false)

        // When
        pointsEngine.startEngine()
        pointsEngine.pauseEngine()

        // Then
        assertFalse(pointsEngine.isRunning.first())
        // Verify that setEngineState was called with true when starting
        verify(medalRepository).setEngineState(true)
    }

    @Test
    fun `resumeEngine restores running state when saved state is true`() = runTest {
        // Given
        whenever(getMedalsUseCase.invoke()).thenReturn(flowOf(testMedals))
        whenever(medalRepository.getEngineState()).thenReturn(true)

        // When
        pointsEngine.resumeEngine()

        // Then
        assertTrue(pointsEngine.isRunning.first())
        verify(medalRepository).setEngineState(true)
    }

    @Test
    fun `resumeEngine does not start when saved state is false`() = runTest {
        // Given
        whenever(medalRepository.getEngineState()).thenReturn(false)

        // When
        pointsEngine.resumeEngine()

        // Then
        assertFalse(pointsEngine.isRunning.first())
    }

    @Test
    fun `clearLevelUpNotification resets last level up medal`() = runTest {
        // When
        pointsEngine.clearLevelUpNotification()

        // Then
        assertEquals(null, pointsEngine.lastLevelUpMedal.first())
    }

    @Test
    fun `startEngine does not start if already running`() = runTest {
        // Given
        whenever(getMedalsUseCase.invoke()).thenReturn(flowOf(testMedals))
        whenever(medalRepository.getEngineState()).thenReturn(false)

        // When
        pointsEngine.startEngine()
        val firstState = pointsEngine.isRunning.first()
        pointsEngine.startEngine() // Try to start again

        // Then
        assertTrue(firstState)
        assertTrue(pointsEngine.isRunning.first())
        // setEngineState should only be called once
        verify(medalRepository).setEngineState(true)
    }

    @Test
    fun `engine stops when no eligible medals available`() = runTest {
        // Given - all medals at max level
        val maxLevelMedals = listOf(
            Medal(1, "Medal 1", 5, 0, 5, "icon1"),
            Medal(2, "Medal 2", 5, 0, 5, "icon2")
        )
        whenever(getMedalsUseCase.invoke()).thenReturn(flowOf(maxLevelMedals))
        whenever(medalRepository.getEngineState()).thenReturn(false)

        // When
        pointsEngine.startEngine()
        advanceUntilIdle()

        // Then
        assertFalse(pointsEngine.isRunning.first())
        verify(medalRepository).setEngineState(false)
    }
}