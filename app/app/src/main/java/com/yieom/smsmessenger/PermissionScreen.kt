package com.yieom.smsmessenger

import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import timber.log.Timber

@Composable
fun PermissionScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    Timber.d("##PermissionScreen, recomposition")

    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        mainViewModel.permissionEventChannel.collect { isGranted ->
            Timber.d("##PermissionScreen, collect permissionEvent: $isGranted")
            if (isGranted) {
                mainViewModel.saveIsPermissionHandled(true)
            }
        }
    }

    Surface(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp),
        color = Color.White,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Permission", style = MaterialTheme.typography.headlineMedium)
            Column(
                modifier =
                    Modifier
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = {
                        executeToRequestPermissions(activity as MainActivity, mainViewModel.requiredPermissions)
                    },
                    modifier =
                        Modifier
                            .width(200.dp)
                            .height(40.dp),
                ) {
                    Text(text = "권한 얻기")
                }
            }
        }
    }
}

const val PERMISSION_REQUEST_CODE = 100001

private fun executeToRequestPermissions(
    activity: MainActivity,
    permissions: Array<String>,
) {
    val shouldShowRationale = activity.shouldShowRationale(permissions.toList())

    Timber.d("##checkPermissions, shouldShowRationale: $shouldShowRationale")
    if (shouldShowRationale) {
        activity.moveToApplicationDetailsSettingsActivity()
    } else {
        activity.requestPermissions(permissions, PERMISSION_REQUEST_CODE)
    }
}
