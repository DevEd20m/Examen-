package com.faztbit.examen.presentation.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.faztbit.examen.ExamenApp
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var mockNavController: NavController

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        hiltRule.inject()
        
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun navigationDestinations_haveCorrectRoutes() {
        // Test that all navigation destinations have the expected routes
        assertThat(NavigationDestination.Profile.route).isEqualTo("profile")
        assertThat(NavigationDestination.Medals.route).isEqualTo("medals")
        assertThat(NavigationDestination.Missions.route).isEqualTo("missions")
        assertThat(NavigationDestination.Streaks.route).isEqualTo("streaks")
        assertThat(NavigationDestination.Album.route).isEqualTo("album")
    }

    @Test
    fun navigationBetweenScreensWorksCorrectly() {
        composeTestRule.setContent {
            ExamenApp()
        }

        // Verify we start on Profile screen
        composeTestRule.onNodeWithText("Vista de Perfil").assertIsDisplayed()

        // Navigate to Medals
        composeTestRule.onNodeWithText("Medallas").performClick()
        composeTestRule.onNodeWithText("Medallas").assertIsDisplayed()

        // Navigate back to Profile
        composeTestRule.onNodeWithText("Perfil").performClick()
        composeTestRule.onNodeWithText("Vista de Perfil").assertIsDisplayed()

        // Test placeholder screens
        composeTestRule.onNodeWithText("Misiones").performClick()
        composeTestRule.onNodeWithText("Próximamente").assertIsDisplayed()

        // Navigate back
        composeTestRule.onNodeWithText("Atrás").performClick()
        composeTestRule.onNodeWithText("Vista de Perfil").assertIsDisplayed()
    }

    @Test
    fun allModuleCardsAreDisplayed() {
        composeTestRule.setContent {
            ExamenApp()
        }

        // Verify all module cards are present
        composeTestRule.onNodeWithText("Medallas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Misiones").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rachas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Álbum").assertIsDisplayed()
    }

    @Test
    fun placeholderScreensDisplayCorrectContent() {
        composeTestRule.setContent {
            ExamenApp()
        }

        // Test Missions placeholder
        composeTestRule.onNodeWithText("Misiones").performClick()
        composeTestRule.onNodeWithText("Misiones").assertIsDisplayed()
        composeTestRule.onNodeWithText("Próximamente").assertIsDisplayed()
        composeTestRule.onNodeWithText("Esta funcionalidad estará disponible pronto").assertIsDisplayed()

        // Go back and test Streaks
        composeTestRule.onNodeWithText("Atrás").performClick()
        composeTestRule.onNodeWithText("Rachas").performClick()
        composeTestRule.onNodeWithText("Rachas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Próximamente").assertIsDisplayed()

        // Go back and test Album
        composeTestRule.onNodeWithText("Atrás").performClick()
        composeTestRule.onNodeWithText("Álbum").performClick()
        composeTestRule.onNodeWithText("Álbum").assertIsDisplayed()
        composeTestRule.onNodeWithText("Próximamente").assertIsDisplayed()
    }

    @Test
    fun navigationBackStack_worksCorrectly() = runTest {
        composeTestRule.setContent {
            ExamenApp()
        }

        // Start at Profile
        composeTestRule.onNodeWithText("Vista de Perfil").assertIsDisplayed()

        // Navigate to Medals
        composeTestRule.onNodeWithText("Medallas").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Medallas").assertIsDisplayed()

        // Navigate to Missions from Medals (if there's a way to do this)
        composeTestRule.onNodeWithText("Perfil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Misiones").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Próximamente").assertIsDisplayed()

        // Navigate back should return to Profile
        composeTestRule.onNodeWithText("Atrás").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Vista de Perfil").assertIsDisplayed()
    }

    @Test
    fun navigationToMedalsScreen_displaysCorrectContent() {
        composeTestRule.setContent {
            ExamenApp()
        }

        // Navigate to Medals screen
        composeTestRule.onNodeWithText("Medallas").performClick()
        composeTestRule.waitForIdle()

        // Verify Medals screen content
        composeTestRule.onNodeWithText("Medallas").assertIsDisplayed()
        
        // Check if back navigation works
        composeTestRule.onNodeWithText("Perfil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Vista de Perfil").assertIsDisplayed()
    }

    @Test
    fun multipleNavigationActions_performCorrectly() {
        composeTestRule.setContent {
            ExamenApp()
        }

        // Perform multiple navigation actions in sequence
        val navigationSequence = listOf(
            "Medallas" to "Medallas",
            "Perfil" to "Vista de Perfil",
            "Misiones" to "Próximamente",
            "Atrás" to "Vista de Perfil",
            "Rachas" to "Próximamente",
            "Atrás" to "Vista de Perfil",
            "Álbum" to "Próximamente",
            "Atrás" to "Vista de Perfil"
        )

        navigationSequence.forEach { (clickText, expectedText) ->
            composeTestRule.onNodeWithText(clickText).performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(expectedText).assertIsDisplayed()
        }
    }

    @Test
    fun navigationDestination_sealedClassStructure_isCorrect() {
        // Test that NavigationDestination is properly structured as a sealed class
        val destinations = listOf(
            NavigationDestination.Profile,
            NavigationDestination.Medals,
            NavigationDestination.Missions,
            NavigationDestination.Streaks,
            NavigationDestination.Album
        )

        // Verify all destinations are instances of NavigationDestination
        destinations.forEach { destination ->
            assertThat(destination).isInstanceOf(NavigationDestination::class.java)
            assertThat(destination.route).isNotEmpty()
        }

        // Verify routes are unique
        val routes = destinations.map { it.route }
        assertThat(routes).containsNoDuplicates()
    }

    @Test
    fun profileScreen_displaysAllNavigationOptions() {
        composeTestRule.setContent {
            ExamenApp()
        }

        // Verify Profile screen shows all navigation options
        composeTestRule.onNodeWithText("Vista de Perfil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Medallas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Misiones").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rachas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Álbum").assertIsDisplayed()
    }
}