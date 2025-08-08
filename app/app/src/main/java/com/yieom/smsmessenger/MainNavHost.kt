package com.yieom.smsmessenger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import timber.log.Timber

sealed class MainDestination(val route: String) {
    object Home: MainDestination("home")
    object Help: MainDestination("Help")
    object Setting: MainDestination("setting")
    object Permission: MainDestination("permission")
}

data class BottomNavItem(
    val route: String,
    val label: String
) {
    companion object {
        fun getBottomNavItems(): List<BottomNavItem> {
            return listOf(
                BottomNavItem(
                    route = MainDestination.Home.route,
                    label = "HOME"
                ),
                BottomNavItem(
                    route = MainDestination.Help.route,
                    label = "HELP"
                ),
                BottomNavItem(
                    route = MainDestination.Setting.route,
                    label = "SETTING"
                ),
            )
        }
    }
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    Timber.d("##MainNavHost, recomposition")
    LaunchedEffect(Unit) {
        mainViewModel.navigationEventChannel.collect { route ->
            Timber.d("##MainNavHost, collect navigationEvent: $route")
            navController.navigate(route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = MainDestination.Home.route,
        modifier = modifier
    ) {
        composable(MainDestination.Home.route) {
            HomeScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(MainDestination.Help.route) {
            HelpScreen()
        }
        composable(MainDestination.Setting.route) {
            SettingScreen()
        }
        composable(MainDestination.Permission.route) {
            PermissionScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}