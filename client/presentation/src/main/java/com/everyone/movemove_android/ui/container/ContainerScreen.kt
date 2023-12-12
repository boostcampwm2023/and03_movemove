package com.everyone.movemove_android.ui.container

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.everyone.movemove_android.R
import com.everyone.movemove_android.ui.StyledText
import com.everyone.movemove_android.ui.screens.home.HomeScreen
import com.everyone.movemove_android.ui.container.navigation.Destination
import com.everyone.movemove_android.ui.container.navigation.Navigator
import com.everyone.movemove_android.ui.profile.ProfileActivity
import com.everyone.movemove_android.ui.screens.uploading_video.UploadingVideoScreen
import com.everyone.movemove_android.ui.theme.BackgroundInDark
import com.everyone.movemove_android.ui.theme.BorderInDark
import com.everyone.movemove_android.ui.theme.InActiveInDark
import com.everyone.movemove_android.ui.theme.Point
import com.everyone.movemove_android.ui.watching_video.WatchingVideoActivity

@Composable
fun MainScreen(navigateToActivity: (intent: Intent) -> Unit) {
    val navController = rememberNavController()
    val navigator = rememberNavigator(navController = navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val mainRoutes = listOf(
        Destination.HOME.route,
        Destination.WATCHING_VIDEO.route,
        Destination.UPLOADING_VIDEO.route,
        Destination.PROFILE.route
    )

    BackOnPressed()
    Scaffold(
        bottomBar = {
            if (currentDestination?.route in mainRoutes) {
                MoveMoveNavigationBar(
                    currentDestination = currentDestination,
                    onNavigate = { navigator.navigateTo(it) },
                    navigateToActivity = navigateToActivity
                )
            }

        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            navScreen(Destination.HOME.route) { HomeScreen(navigateToActivity = navigateToActivity) }
            navScreen(Destination.UPLOADING_VIDEO.route) { UploadingVideoScreen() }
        }
    }
}

@Composable
fun MoveMoveNavigationBar(
    currentDestination: NavDestination?,
    onNavigate: (Destination) -> Unit,
    navigateToActivity: (intent: Intent) -> Unit
) {
    val context = LocalContext.current

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
                        onClick = {
                            when (destination) {
                                Destination.WATCHING_VIDEO -> {
                                    navigateToActivity(
                                        WatchingVideoActivity.newIntent(
                                            context = context,
                                            videosList = null,
                                            page = null
                                        )
                                    )
                                }

                                Destination.PROFILE -> {
                                    navigateToActivity(
                                        ProfileActivity.newIntent(
                                            context = context,
                                            uuid = null
                                        )
                                    )
                                }

                                else -> onNavigate(destination)
                            }
                        },
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

fun NavGraphBuilder.navScreen(
    destination: String,
    content: @Composable () -> Unit
) {
    composable(
        route = destination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) { content() }
}

@Composable
fun BackOnPressed() {
    val context = LocalContext.current
    var backPressedState by remember { mutableStateOf(true) }
    var backPressedTime = 0L

    BackHandler(enabled = backPressedState) {
        if (System.currentTimeMillis() - backPressedTime <= 1500L) {
            (context as Activity).finish()
        } else {
            backPressedTime = System.currentTimeMillis()
            backPressedState = true
            Toast.makeText(context, R.string.on_back_pressed_message, Toast.LENGTH_SHORT).show()
        }
    }
}