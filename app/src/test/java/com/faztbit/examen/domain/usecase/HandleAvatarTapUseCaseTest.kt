package com.faztbit.examen.domain.usecase

import com.faztbit.examen.domain.repository.MedalRepository
import com.faztbit.examen.domain.usecase.ResetAllProgressUseCase

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HandleAvatarTapUseCaseTest {

    @Mock
    private lateinit var medalRepository: MedalRepository

    @Mock
    private lateinit var resetAllProgressUseCase: ResetAllProgressUseCase

    private lateinit var handleAvatarTapUseCase: HandleAvatarTapUseCaseImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        handleAvatarTapUseCase = HandleAvatarTapUseCaseImpl(medalRepository, resetAllProgressUseCase)
    }

    @Test
    fun `first tap does not trigger reset`() = runTest {
        // Given
        whenever(medalRepository.getAvatarTapCount()).thenReturn(0)
        whenever(medalRepository.getLastTapTime()).thenReturn(0L)

        // When
        val shouldReset = handleAvatarTapUseCase()

        // Then
        assertFalse(shouldReset)
    }

    @Test
    fun `five consecutive taps within timeout triggers reset`() = runTest {
        // Given
        val currentTime = System.currentTimeMillis()
        whenever(medalRepository.getAvatarTapCount()).thenReturn(4) // Will become 5
        whenever(medalRepository.getLastTapTime()).thenReturn(currentTime - 1000) // Within timeout

        // When
        val shouldReset = handleAvatarTapUseCase()

        // Then
        assertTrue(shouldReset)
        verify(resetAllProgressUseCase).invoke()
    }

    @Test
    fun `taps outside timeout window reset counter`() = runTest {
        // Given
        val currentTime = System.currentTimeMillis()
        whenever(medalRepository.getAvatarTapCount()).thenReturn(3)
        whenever(medalRepository.getLastTapTime()).thenReturn(currentTime - 10000) // Outside timeout

        // When
        val shouldReset = handleAvatarTapUseCase()

        // Then
        assertFalse(shouldReset)
        verify(medalRepository).updateAvatarTapCount(1) // Verify tap count was reset to 1
        verify(medalRepository).updateLastTapTime(any())
    }

    @Test
    fun `four taps within timeout does not trigger reset`() = runTest {
        // Given
        val currentTime = System.currentTimeMillis()
        whenever(medalRepository.getAvatarTapCount()).thenReturn(3) // Will become 4
        whenever(medalRepository.getLastTapTime()).thenReturn(currentTime - 1000) // Within timeout

        // When
        val shouldReset = handleAvatarTapUseCase()

        // Then
        assertFalse(shouldReset)
    }
}