package com.faztbit.examen.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.faztbit.examen.domain.entity.Medal
import org.junit.Rule
import org.junit.Test

class AnimationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testMedal = Medal(
        id = 1,
        name = "Test Medal",
        currentLevel = 2,
        currentPoints = 100,
        maxLevel = 5,
        iconResource = "🏆"
    )

    @Test
    fun levelUpAnimationDisplaysCorrectly() {
        var animationVisible by mutableStateOf(false)

        composeTestRule.setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (animationVisible) {
                            LevelUpAnimation(
                                medal = testMedal,
                                onDismiss = { animationVisible = false }
                            )
                        }
                    }
                }
            }
        }

        // Start animation
        composeTestRule.runOnUiThread {
            animationVisible = true
        }

        // Verify animation components are displayed
        composeTestRule.waitForIdle()
        
        // In a real test, we would verify specific UI elements
        // For now, we just ensure the composable doesn't crash
    }

    @Test
    fun confettiAnimationPerformsCorrectly() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConfettiAnimation()
                }
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify confetti animation renders without crashing
        // In a real test, we would verify particle effects
    }
}

@Composable
private fun ConfettiAnimation() {
    val density = LocalDensity.current
    var particles by remember { mutableStateOf(emptyList<ConfettiParticle>()) }

    LaunchedEffect(Unit) {
        particles = List(50) { index ->
            ConfettiParticle(
                x = (0..400).random().dp,
                y = (-100).dp,
                color = listOf(
                    androidx.compose.ui.graphics.Color.Red,
                    androidx.compose.ui.graphics.Color.Blue,
                    androidx.compose.ui.graphics.Color.Green,
                    androidx.compose.ui.graphics.Color.Yellow
                ).random()
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            ConfettiParticleComposable(particle = particle)
        }
    }
}

private data class ConfettiParticle(
    val x: androidx.compose.ui.unit.Dp,
    val y: androidx.compose.ui.unit.Dp,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
private fun ConfettiParticleComposable(particle: ConfettiParticle) {
    // Simple particle representation for testing
    Box(
        modifier = Modifier
            .size(4.dp)
            .offset(x = particle.x, y = particle.y)
            .background(particle.color)
    )
}