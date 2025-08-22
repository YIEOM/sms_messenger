package com.yieom.smsmessenger

import androidx.compose.foundation.layout.Box
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

@Composable
fun SettingScreen() {
    Surface(
        modifier =
            Modifier
                .fillMaxSize(),
        color = Color.White,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = "Setting",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "버전: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
            )
        }
    }
}
