package com.yieom.smsmessenger

import android.os.Bundle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yieom.smsmessenger.ui.theme.SMSMessengerTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMSMessengerTheme {
                val storagePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (isGranted) {
                            Toast
                                .makeText(this, "Permission granted", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val mainViewModel: MainViewModel = hiltViewModel()
                    MainScreenWithNav(mainViewModel = mainViewModel)
                }


            }
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
        MainNavHost(navController = navController, modifier = Modifier.padding(contentPadding), mainViewModel = mainViewModel)
    }
}

@Composable
fun MainNavigationBar(
    navController: NavHostController
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
