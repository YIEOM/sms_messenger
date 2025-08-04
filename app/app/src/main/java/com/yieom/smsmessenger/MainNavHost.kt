package com.yieom.smsmessenger

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
) {
    NavHost(
        navController = navController,
        startDestination = MainDestination.Home.route,
        modifier = modifier
    ) {
        composable(MainDestination.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(MainDestination.Help.route) {
            HelpScreen()
        }
        composable(MainDestination.Setting.route) {
            SettingScreen()
        }
        composable(MainDestination.Permission.route) {
            PermissionScreen(navController = navController)
        }
    }
}