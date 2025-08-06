package com.yieom.smsmessenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import timber.log.Timber

@Composable
fun PermissionScreen(navController: NavController, mainViewModel: MainViewModel) {
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
            Text(text = "Permission Screen", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
            Button(onClick = {
                mainViewModel.hasPermissions = true
                Timber.d("##PermissionScreen, hasPermission: ${mainViewModel.hasPermissions()}")
                navController.popBackStack()
            },
                modifier = Modifier.width(200.dp).height(40.dp)) {
                Text(text = "Grant permission", color = Color.Green)
            }
        }
    }
}