package com.faztbit.examen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faztbit.examen.presentation.navigation.NavigationDestination
import com.faztbit.examen.presentation.screen.MedalsScreen
import com.faztbit.examen.presentation.screen.PlaceholderScreen
import com.faztbit.examen.presentation.screen.ProfileScreen
import com.faztbit.examen.ui.theme.ExamenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenTheme {
                ExamenApp()
            }
        }
    }
}

@Composable
fun ExamenApp() {
    val navController: NavHostController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationDestination.Profile.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(NavigationDestination.Profile.route) {
                ProfileScreen(
                    onNavigateToMedals = {
                        navController.navigate(NavigationDestination.Medals.route)
                    },
                    onNavigateToMissions = {
                        navController.navigate(NavigationDestination.Missions.route)
                    },
                    onNavigateToStreaks = {
                        navController.navigate(NavigationDestination.Streaks.route)
                    },
                    onNavigateToAlbum = {
                        navController.navigate(NavigationDestination.Album.route)
                    }
                )
            }

            composable(NavigationDestination.Medals.route) {
                MedalsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavigationDestination.Missions.route) {
                PlaceholderScreen(
                    title = "Misiones",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavigationDestination.Streaks.route) {
                PlaceholderScreen(
                    title = "Rachas",
                    icon = Icons.Default.Whatshot,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavigationDestination.Album.route) {
                PlaceholderScreen(
                    title = "Álbum",
                    icon = Icons.Default.PhotoLibrary,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}