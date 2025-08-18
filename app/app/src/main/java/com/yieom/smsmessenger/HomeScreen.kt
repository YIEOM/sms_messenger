package com.yieom.smsmessenger

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    Timber.d("##HomeScreen, recomposition")

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        homeViewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val sheetUrlText by homeViewModel.sheetUrlTextState.collectAsState()

    Surface(
        modifier =
            Modifier
                .fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = sheetUrlText,
                    onValueChange = { newText ->
                        homeViewModel.setSheetUrlText(newText)
                    },
                    label = { Text("스프레드 시트 URL") },
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors =
                        TextFieldDefaults.colors(
                            // 입력된 텍스트 색상
                            focusedTextColor = Color.Blue, // Use focusedTextColor for text color when focused
                            unfocusedTextColor = Color.DarkGray, // Optional: specify unfocused text color
                            // 커서 색상
                            cursorColor = Color.Blue,
                            // 포커스가 있을 때의 라벨 색상
                            focusedLabelColor = Color.Blue,
                            unfocusedLabelColor = Color.DarkGray, // Optional: specify unfocused label color
                            // 포커스가 있을 때의 테두리 색상
                            focusedIndicatorColor = Color.Blue, // Use focusedIndicatorColor for the border/indicator
                            unfocusedIndicatorColor = Color.LightGray, // Optional: specify unfocused border/indicator color
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Done,
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            },
                        ),
                )
                Button(onClick = {
                    mainViewModel.requestSignIn()
                }) {
                    Text("확인")
                }
            }
            Button(onClick = {
                homeViewModel.sendMultipleSms(homeViewModel.getSmsDataList())
            }) {
                Text("SMS 보내기")
            }
        }
    }
}

// A simple mock Application for previews
class MockApplication : Application() {
    // You can override methods here if your ViewModel interacts with them,
    // but for basic previews, an empty class often suffices.
    override fun onCreate() {
        super.onCreate()
        // Mock initialization if needed
    }
}

@Composable
@Preview(showBackground = true) // 미리보기에 배경색을 표시하여 UI를 더 잘 볼 수 있도록 합니다.
fun HomeScreenPreview() {
    // 미리보기에서는 실제 NavController나 ViewModel의 전체 기능이 필요하지 않을 수 있습니다.
    // 간단한 mock 객체나 rememberNavController()를 사용할 수 있습니다.
    val navController = rememberNavController()
    val mockApplicationContext = MockApplication()

    // ViewModel의 경우, 미리보기용으로 hiltViewModel()을 직접 호출하거나,
    // 테스트용으로 간단한 mock ViewModel 인스턴스를 만들 수 있습니다.
    // Hilt ViewModel을 미리보기에서 사용하려면 추가 설정이 필요할 수 있으며,
    // 간단한 미리보기에서는 ViewModel의 의존성을 제거한 버전을 사용하거나
    // 필요한 최소한의 데이터만 가진 mock 객체를 전달하는 것이 더 쉬울 수 있습니다.

    // 예시 1: 실제 ViewModel 인스턴스 사용 (Hilt 설정 및 의존성에 따라 동작 여부 다름)
    // val mainViewModel: MainViewModel = hiltViewModel()
    // val homeViewModel: HomeViewModel = hiltViewModel()

    // 예시 2: 간단한 Mock ViewModel 사용 (ViewModel 로직이 복잡하지 않을 경우)
    // 이 방법은 ViewModel의 실제 로직을 테스트하는 것이 아니라 UI 모양만 보는 데 적합합니다.
    class MockMainViewModel : MainViewModel(/* 필요한 의존성 mock 객체 전달 */) {
        // 미리보기에 필요한 최소한의 동작이나 상태만 구현
    }

    class MockHomeViewModel : HomeViewModel(mockApplicationContext) {
        // 미리보기에 필요한 최소한의 동작이나 상태만 구현
        override val sheetUrlTextState = kotlinx.coroutines.flow.MutableStateFlow("미리보기 URL")
        // ... 다른 필요한 상태나 함수 override
    }

    val mainViewModel = MockMainViewModel() // 실제 MainViewModel 생성 방식에 따라 수정
    val homeViewModel = MockHomeViewModel() // 실제 HomeViewModel 생성 방식에 따라 수정

    // MaterialTheme으로 감싸서 앱의 전체적인 테마를 적용하는 것이 좋습니다.
    // SMSMessengerTheme이 있다면 그것을 사용하세요. 없다면 기본 MaterialTheme을 사용합니다.
    MaterialTheme {
        // 만약 SMSMessengerTheme 같은 커스텀 테마가 있다면 그것으로 교체하세요.
        HomeScreen(
            navController = navController,
            mainViewModel = mainViewModel,
            homeViewModel = homeViewModel,
        )
    }
}
