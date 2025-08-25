package com.yieom.smsmessenger

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yieom.smsmessenger.dataStore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class MainViewModel
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
    ) : ViewModel() {
        init {
            Timber.d("##init")
            loadSheetUrl()
        }

        fun loadSheetUrl() {
            viewModelScope.launch {
                val sheetUrl = dataStoreRepository.getUrl()
                _sheetUrlTextState.value = sheetUrl
            }
        }

        fun saveSheetUrl() {
            viewModelScope.launch {
                dataStoreRepository.saveSheetUrl(sheetUrlTextState.value)
            }
        }

        val requiredPermissions =
            arrayOf(
                Manifest.permission.SEND_SMS,
            )

        private val _navigationEventChannel = Channel<String>(Channel.BUFFERED) // Or Channel.RENDEZVOUS
        val navigationEventChannel = _navigationEventChannel.receiveAsFlow()

        val _permissionEventChannel = Channel<Boolean>(Channel.BUFFERED)
        val permissionEventChannel = _permissionEventChannel.receiveAsFlow()

        fun sendPermissionEventChannel(value: Boolean) {
            viewModelScope.launch {
                _permissionEventChannel.send(value)
            }
        }

        fun onResumePermissionCheck(isGrantedRequiredPermissions: Boolean) {
            viewModelScope.launch {
                if (!isGrantedRequiredPermissions) {
                    _navigationEventChannel.send(MainDestination.Permission.route)
                } else {
                    _navigationEventChannel.send(MainDestination.Home.route)
                }
            }
        }

        private val _requestSignInChannel = Channel<Boolean>() // Toast 메시지를 전달할 채널
        val requestSignInChannel = _requestSignInChannel.receiveAsFlow() // UI에서 관찰할 Flow

        fun requestSignIn() {
            viewModelScope.launch {
                _requestSignInChannel.send(true)
            }
        }

        private val _toastEventChannel = Channel<String>() // Toast 메시지를 전달할 채널
        val toastEvent = _toastEventChannel.receiveAsFlow() // UI에서 관찰할 Flow

        private fun showToast(message: String) {
            viewModelScope.launch {
                _toastEventChannel.send(message)
            }
        }

        private val _sheetUrlTextState = MutableStateFlow("")
        open val sheetUrlTextState = _sheetUrlTextState.asStateFlow()

        fun setSheetUrlText(text: String) {
            _sheetUrlTextState.value = text
        }

        fun checkSpreadSheetUrl(): Pair<Boolean, String> {
            val text = sheetUrlTextState.value
            val regex = "d/([a-zA-Z0-9_-]+)/edit".toRegex()
            val matchResult = regex.find(text)

            return if (matchResult != null) {
                val sheetId = matchResult.groupValues[1]
                Pair(true, sheetId)
            } else {
                Pair(false, "")
            }
        }

        private val _cellsState = MutableStateFlow(listOf<List<String>>())
        open val cellsState = _cellsState.asStateFlow()

        fun checkCells(cells: List<List<String>>) {
            val regex = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$".toRegex()
            val result = cells.filter { it.size == 4 && regex.find(it[2]) != null }
            _cellsState.value = result
        }
    }
