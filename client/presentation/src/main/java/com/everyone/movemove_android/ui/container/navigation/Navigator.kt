package com.everyone.movemove_android.ui.container.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

class Navigator(private val navController: NavController) {

    fun navigateTo(destination: Destination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}
