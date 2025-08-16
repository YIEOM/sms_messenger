package com.yieom.smsmessenger

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun HomeScreen(navController: NavController, mainViewModel: MainViewModel, homeViewModel: HomeViewModel = hiltViewModel()) {
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
            OutlinedTextField(
                value = sheetUrlText,
                onValueChange = { newText ->
                    homeViewModel.onSpreadSheetUrlTextChanged(newText)
                },
                label = { Text("스프레드 시트 URL") },
                modifier = Modifier.padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
            Button(onClick = {
                mainViewModel.requestSignIn()
            }) {
                Text("Google 로그인")
            }
            Button(onClick = {
                homeViewModel.sendMultipleSms(homeViewModel.getSmsDataList())
            }) {
                Text("SMS 보내기")
            }
        }
    }
}