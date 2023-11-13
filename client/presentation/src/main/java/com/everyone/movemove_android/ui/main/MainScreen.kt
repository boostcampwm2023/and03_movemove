package com.everyone.movemove_android.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.main.home.HomeScreen
import com.everyone.movemove_android.ui.main.my.MyScreen
import com.everyone.movemove_android.ui.main.navigation.Destination
import com.everyone.movemove_android.ui.main.navigation.rememberNavigator
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoScreen
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoScreen
import com.everyone.movemove_android.ui.theme.InActiveInDark
import com.everyone.movemove_android.ui.theme.Point

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navigator = rememberNavigator(navController = navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(bottomBar = {
        MoveMoveNavigationBar(
            currentDestination = currentDestination,
            onNavigate = { navigator.navigateTo(it) },
        )
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Destination.HOME.route) { HomeScreen() }
            composable(Destination.WATCHING_VIDEO.route) { WatchingVideoScreen() }
            composable(Destination.UPLOADING_VIDEO.route) { UploadingVideoScreen() }
            composable(Destination.MY.route) { MyScreen() }
        }
    }
}

@Composable
fun MoveMoveNavigationBar(
    currentDestination: NavDestination?,
    onNavigate: (Destination) -> Unit
) {
    NavigationBar {
        Destination.values().forEach { destination ->

            val selected =
                currentDestination?.hierarchy?.any { it.route == destination.route } == true

            NavigationBarItem(icon = {
                Icon(
                    painter = painterResource(id = destination.iconRes),
                    contentDescription = null,
                    tint = if (selected) Point else InActiveInDark
                )
            }, label = {
                StyledText(
                    text = stringResource(id = destination.labelResId),
                    style = MaterialTheme.typography.labelSmall
                )
            }, selected = false, onClick = { onNavigate(destination) })
        }
    }
}
