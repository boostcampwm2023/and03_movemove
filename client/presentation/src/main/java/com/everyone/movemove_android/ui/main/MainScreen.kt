package com.everyone.movemove_android.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
import com.everyone.movemove_android.ui.main.navigation.Navigator
import com.everyone.movemove_android.ui.main.uploading_video.UploadingVideoScreen
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoScreen
import com.everyone.movemove_android.ui.theme.BackgroundInDark
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.InActiveInDark
import com.everyone.movemove_android.ui.theme.Point
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navigator = rememberNavigator(navController = navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            MoveMoveNavigationBar(
                currentDestination = currentDestination,
                onNavigate = { navigator.navigateTo(it) },
            )
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier.padding(innerPadding)
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

    Column {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = BorderInDark)
        )

        CompositionLocalProvider(LocalRippleTheme.provides(object : RippleTheme {
            @Composable
            override fun defaultColor() = Color.Unspecified

            @Composable
            override fun rippleAlpha() = RippleAlpha(0f, 0f, 0f, 0f)
        })) {
            NavigationBar(
                containerColor = BackgroundInDark
            ) {
                Destination.values().forEach { destination ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == destination.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.iconRes),
                                contentDescription = null,
                                tint = if (selected) Point else InActiveInDark
                            )
                        },
                        label = {
                            StyledText(
                                text = stringResource(id = destination.labelResId),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = false,
                        onClick = { onNavigate(destination) },
                        interactionSource = MutableInteractionSource()
                    )
                }
            }
        }
    }
}

@Composable
fun rememberNavigator(navController: NavController) =
    remember(navController) { Navigator(navController) }