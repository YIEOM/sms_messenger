package com.yieom.smsmessenger

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import timber.log.Timber

@Composable
fun HomeScreen(navController: NavController, mainViewModel: MainViewModel) {
    Timber.d("##HomeScreen, recomposition")

    val context = LocalContext.current

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
            Button(onClick = {
                executeToSendSMS(context)
            }) {
                Text("SMS 보내기")
            }
        }
    }
}

private fun executeToSendSMS(context: Context) {
    sendSms(context, "010-4002-5160", "Jetpack Compose에서 보낸 테스트 메시지!")

}

private fun sendSms(context: Context, phoneNumber: String, message: String) {
    try {
        val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Toast.makeText(context, "SMS 전송 완료!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "SMS 전송 실패: ${e.message}", Toast.LENGTH_LONG).show()
    }
}