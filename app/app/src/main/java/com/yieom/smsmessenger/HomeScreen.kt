package com.yieom.smsmessenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import timber.log.Timber

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    Timber.d("##HomeScreen, hasPermission: ${viewModel.hasPermissions()}")
    if (!viewModel.hasPermissions()) {
        navController.navigate(MainDestination.Permission.route)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
        }
    }
}