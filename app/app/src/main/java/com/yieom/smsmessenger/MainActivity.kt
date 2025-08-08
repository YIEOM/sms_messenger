package com.yieom.smsmessenger

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yieom.smsmessenger.ui.theme.SMSMessengerTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            SMSMessengerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainScreenWithNav(mainViewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onResumePermissionCheck(isGrantedRequiredPermissions())
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                Timber.d("##request permissions: ${permissions.toList()}, grantResults: ${grantResults.toList()}")
                if ((grantResults.isNotEmpty())) {
                    for (i in permissions.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            viewModel.requiredPermissions.forEach {
                                if (it == permissions[i]) {
                                    return
                                }
                            }
                        }
                    }
                    Timber.d("##request permissions: ${isGrantedRequiredPermissions()}")
                    if (isGrantedRequiredPermissions()) {
                        viewModel.sendPermissionEventChannel(true)
                    }
                }
            }

            else -> {
            }
        }
    }

    private fun isGrantedRequiredPermissions(): Boolean {
        return viewModel.requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun shouldShowRationale(permissions: List<String>): Boolean {
        var shouldShowRationale = false
        run {
            permissions.forEach { permission ->
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Timber.d("##checkPermissions, permission: $permission")
                    shouldShowRationale = true
                    return@run
                }
            }
        }
        return shouldShowRationale
    }

    fun moveToApplicationDetailsSettingsActivity() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package",packageName, null)
            startActivity(this)
        }
    }
}

@Composable
fun MainScreenWithNav(modifier: Modifier = Modifier, mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = currentRoute in BottomNavItem.getBottomNavItems().map { it.route }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                MainNavigationBar(navController)
            }
        }
    ) { contentPadding ->
        SideEffect { Timber.d("##MainScreenWithNav, MainNavHost recomposed") }
        MainNavHost(navController = navController, modifier = Modifier.padding(contentPadding), mainViewModel = mainViewModel)
    }
}

@Composable
fun MainNavigationBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        BottomNavItem.getBottomNavItems().forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(route = destination.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                },
                label = { Text(destination.label) }
            )
        }
    }
}
