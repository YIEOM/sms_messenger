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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel
    @Inject
    constructor(
        @ApplicationContext private val applicationContext: Context,
    ) : ViewModel() {
        private val _toastEventChannel = Channel<String>() // Toast 메시지를 전달할 채널
        val toastEvent = _toastEventChannel.receiveAsFlow() // UI에서 관찰할 Flow

        init {
            Timber.d("##init")
        }

        fun sendMultipleSms() {
            val smsMessages = homeUiState.value.smsMessages
            if (smsMessages.isEmpty()) {
                showToast("보낼 SMS가 없습니다.")
                return
            }

            smsMessages.forEach { smsMessage ->
                viewModelScope.launch(Dispatchers.IO) {
                    sendSmsInBackground(applicationContext, smsMessage)
                }
            }
            showToast("${smsMessages.size}개의 SMS 전송을 시작합니다.")
        }

        private val _homeUiState =
            MutableStateFlow<HomeUiState>(
                HomeUiState(
                    smsMessages = listOf(),
                ),
            ) // Toast 메시지를 전달할 채널
        val homeUiState = _homeUiState.asStateFlow() // UI에서 관찰할 Flow

        private fun sendSmsInBackground(
            context: Context,
            smsMessage: SmsMessage,
        ) {
            try {
                val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
                smsManager.sendTextMessage(smsMessage.phoneNumber, null, smsMessage.message, null, null)
                changeIsSuccess(smsMessage.idx, "성공")
            } catch (e: Exception) {
                changeIsSuccess(smsMessage.idx, "실패")
            }
        }

        private fun showToast(message: String) {
            viewModelScope.launch {
                _toastEventChannel.send(message)
            }
        }

        private fun changeIsSuccess(
            idx: Int,
            isSuccess: String,
        ) {
            _homeUiState.update {
                it.copy(
                    smsMessages =
                        _homeUiState.value.smsMessages.map { smsMessage ->
                            if (smsMessage.idx == idx) {
                                smsMessage.copy(isSuccess = isSuccess)
                            } else {
                                smsMessage
                            }
                        },
                )
            }
        }

        fun changeSheetUrl(url: String) {
            _homeUiState.update {
                it.copy(sheetUrl = url)
            }
        }

        fun changeSmsMessages(list: List<List<String>>) {
            _homeUiState.update {
                it.copy(
                    smsMessages =
                        list.map { cellMessage ->
                            SmsMessage(
                                idx = cellMessage[0].toInt(),
                                name = cellMessage[1],
                                phoneNumber = cellMessage[2],
                                message = cellMessage[3],
                            )
                        },
                )
            }
        }
    }

data class HomeUiState(
    val sheetUrl: String = "",
    val smsMessages: List<SmsMessage>,
)

data class SmsMessage(
    val idx: Int,
    val name: String,
    val phoneNumber: String,
    val message: String,
    var isSuccess: String? = null,
)
