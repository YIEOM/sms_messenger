package com.yieom.smsmessenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun HelpScreen() {
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
            Text(text = "Help Screen", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
            Text(text = "스프레드 시트 URL 가져오기\n스프레드 시트 앱에서 사용하고자 하는 파일을 선택->\n오른 상단의 메뉴에서 공유 및 내보내기 선택->\n링크 복사 선택", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
        }
    }
}