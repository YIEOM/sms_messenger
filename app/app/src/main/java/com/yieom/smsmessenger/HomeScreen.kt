package com.yieom.smsmessenger

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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

    LaunchedEffect(key1 = Unit) {
        mainViewModel.sheetUrlTextState.collectLatest {
            homeViewModel.changeSheetUrl(it)
        }
    }

    LaunchedEffect(key1 = Unit) {
        mainViewModel.cellsState.collectLatest {
            homeViewModel.changeSmsMessages(it)
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val homeUiState by homeViewModel.homeUiState.collectAsState()

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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Home", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = homeUiState.sheetUrl,
                    onValueChange = { newText ->
                        mainViewModel.setSheetUrlText(newText)
                    },
                    maxLines = 1,
                    label = { Text("스프레드 시트 URL") },
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
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
                Button(
                    modifier = Modifier.offset(6.dp),
                    onClick = {
                        mainViewModel.requestSignIn()
                    },
                ) {
                    Text(
                        text = "가져오기",
                        maxLines = 1,
                    )
                }
            }
            LazyColumn(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
            ) {
                items(homeUiState.smsMessages) { cellMessage ->
                    CellMessage(cellMessage)
                }
            }
            Button(
                onClick = {
                    homeViewModel.sendMultipleSms()
                },
            ) {
                Text("SMS 보내기")
            }
        }
    }
}

@Composable
fun CellMessage(smsMessage: SmsMessage) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = smsMessage.idx.toString(),
            color = Color.Black,
            maxLines = 1,
        )
        Text(
            text = smsMessage.name,
            color = Color.Black,
            maxLines = 1,
        )
        Text(
            text = smsMessage.phoneNumber,
            color = Color.Black,
            maxLines = 1,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = smsMessage.message,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = smsMessage.isSuccess ?: "",
            color = Color.Blue,
            maxLines = 1,
        )
    }
}
