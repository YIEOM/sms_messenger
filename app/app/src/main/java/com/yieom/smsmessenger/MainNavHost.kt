package com.yieom.smsmessenger

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

enum class MainDestination(
    val route: String,
    val contentDescription: String
) {
    HOME("home", "Home"),
    PERMISSION("permission", "Permission"),
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: MainDestination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(MainDestination.HOME.route) {
            HomeScreen()
        }
        composable(MainDestination.PERMISSION.route) {
            PermissionScreen()
        }
    }
}