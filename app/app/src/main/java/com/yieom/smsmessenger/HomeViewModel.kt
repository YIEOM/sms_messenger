package com.yieom.smsmessenger

import android.content.Context
import android.telephony.SmsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {
    private val _toastEventChannel = Channel<String>() // Toast 메시지를 전달할 채널
    val toastEvent = _toastEventChannel.receiveAsFlow() // UI에서 관찰할 Flow

    init {
        Timber.d("##init")
    }

    fun sendMultipleSms(smsDataList: List<SmsData>) {
        if (smsDataList.isEmpty()) {
            showToast("보낼 SMS가 없습니다.")
            return
        }

        smsDataList.forEach { smsData ->
            viewModelScope.launch(Dispatchers.IO) {
                Timber.d("Sending SMS to ${smsData.phoneNumber} in coroutine on ${Thread.currentThread().name}")
                sendSmsInBackground(applicationContext, smsData)
            }
        }
        showToast("${smsDataList.size}개의 SMS 전송을 시작합니다.")
    }

    private fun sendSmsInBackground(context: Context, smsData: SmsData) {
        try {
            val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(smsData.phoneNumber, null, smsData.message, null, null)
            showToast("SMS 전송 완료!")
        } catch (e: Exception) {
            showToast("SMS 전송 실패: ${e.message}")
        }
    }

    fun getSmsDataList(): List<SmsData> {
        return listOf(
            SmsData("010-4002-5160", "Jetpack Compose에서 보낸 테스트 메시지! 1"),
            SmsData("010-4002-5160", "Jetpack Compose에서 보낸 테스트 메시지! 2"),
        )
    }

    private val _sheetUrlTextState = MutableStateFlow("")
    val sheetUrlTextState = _sheetUrlTextState.asStateFlow()

    fun onSpreadSheetUrlTextChanged(newText: String): Boolean {
        _sheetUrlTextState.value = newText

        val regex = "d/([a-zA-Z0-9_-]+)/edit".toRegex()
        val matchResult = regex.find(newText)

        viewModelScope.launch {
            if (matchResult != null) {
                val sheetId = matchResult.groupValues[1]

                _toastEventChannel.send("다음의 링크가 복사되었습니다. : $sheetId")
            } else {
                _toastEventChannel.send("잘못된 링크를 복사했습니다.")
            }
        }
        return false
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastEventChannel.send(message)
        }
    }
}

data class SmsData(val phoneNumber: String, val message: String)