package com.yieom.smsmessenger

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.yieom.smsmessenger.ui.theme.SMSMessengerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    color = Color.White,
                ) {
                    RootComposable(viewModel = viewModel)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.requestSignInChannel.collect {
                    if (it) {
                        requestSignIn()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            if (viewModel.isPermissionHandled().first()) {
                viewModel.onResumePermissionCheck(isGrantedRequiredPermissions())
            }
        }
    }

    @Deprecated(
        "This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.",
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
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

    private fun isGrantedRequiredPermissions(): Boolean =
        viewModel.requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
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
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }

    private fun requestSignIn() {
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                // 여기서 Sheets API의 읽기 권한을 요청합니다.
                .requestScopes(Scope(SheetsScopes.SPREADSHEETS_READONLY))
                .build()

        val signInClient = GoogleSignIn.getClient(this, gso)
        // 로그인 인텐트 실행
        signInLauncher.launch(signInClient.signInIntent)
    }

    private val signInLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                // 로그인이 성공하면 API 호출
                handleSignInResult(task)
            }
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            lifecycleScope.launch(Dispatchers.IO) {
                // 1. 인증 정보(Credential) 생성
                val credential =
                    GoogleAccountCredential
                        .usingOAuth2(
                            this@MainActivity,
                            listOf(SheetsScopes.SPREADSHEETS_READONLY),
                        ).apply {
                            selectedAccount = account.account
                        }

                // 2. Sheets 서비스 객체 빌드
                val sheetsService =
                    Sheets
                        .Builder(
                            AndroidHttp.newCompatibleTransport(),
                            GsonFactory(),
                            credential,
                        ).setApplicationName(applicationContext.packageName) // 앱 이름 설정
                        .build()

                Timber.w("##handleSignInResult sign in: $sheetsService")
                // 3. 데이터 읽기
                readDataFromSheet(sheetsService)
            }
        } catch (e: ApiException) {
            // 로그인 실패 처리
            Timber.w("##signInResult:failed code=" + e.statusCode)
        }
    }

    private suspend fun readDataFromSheet(sheetsService: Sheets) {
        // 읽어올 스프레드시트 ID와 범위를 지정합니다.
        val result = viewModel.checkSpreadSheetUrl()
        Timber.i("##readDataFromSheet, result $result")
        if (result.first) {
            viewModel.saveSheetUrl()

            val range = "문자메시지!A2:D" // 예: 시트1의 A1부터 D10까지

            try {
                val response =
                    sheetsService
                        .spreadsheets()
                        .values()
                        .get(result.second, range)
                        .execute()

                val values = response.getValues() // 데이터가 2차원 리스트로 반환됩니다.

                if (values != null && values.isNotEmpty()) {
                    // UI 스레드에서 결과 표시
                    withContext(Dispatchers.Main) {
                        values.forEach { _ ->
                            Timber.i("##readDataFromSheet ${values.map { it.map { cell -> cell.toString() } }}")
                        }
                        viewModel.checkCells(values as List<List<String>>)
                    }
                } else {
                    Timber.d("##No data found.")
                }
            } catch (e: Exception) {
                // API 호출 중 에러 처리
                e.printStackTrace()
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "URL이 잘못 입력되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun RootComposable(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "initial_loading_route", // 임시 시작점 또는 실제 로직에 따른 시작점
    ) {
        composable(MainDestination.Permission.route) {
            PermissionScreen(navController, viewModel)
        }

        composable(MainDestination.MainScreenWithNav.route) {
            // MainDestination에 이 라우트 정의 필요
            MainScreenWithNav(viewModel = viewModel)
        }

        composable("initial_loading_route") {
        }
    }

    val isPermissionHandled by viewModel.isPermissionHandled().collectAsState(false)

    if (isPermissionHandled) {
        MainScreenWithNav(viewModel = viewModel)
    } else {
        navController.navigate(route = MainDestination.Permission.route)
    }
}

@Composable
fun MainScreenWithNav(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = currentRoute in BottomNavItem.getBottomNavItems().map { it.route }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                MainNavigationBar(navController)
            }
        },
    ) { contentPadding ->
        SideEffect { Timber.d("##MainScreenWithNav, MainNavHost recomposed") }
        MainNavHost(navController = navController, modifier = Modifier.padding(contentPadding), mainViewModel = viewModel)
    }
}

@Composable
fun MainNavigationBar(navController: NavHostController) {
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
                label = { Text(destination.label) },
            )
        }
    }
}
