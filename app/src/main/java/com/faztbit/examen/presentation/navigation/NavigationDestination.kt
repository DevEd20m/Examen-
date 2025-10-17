package com.faztbit.examen.presentation.navigation

sealed class NavigationDestination(val route: String) {
    object Profile : NavigationDestination("profile")
    object Medals : NavigationDestination("medals")
    object Missions : NavigationDestination("missions")
    object Streaks : NavigationDestination("streaks")
    object Album : NavigationDestination("album")
}