package com.yieom.smsmessenger

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {
    init {
        Timber.d("##init")
    }

    val requiredPermissions = arrayOf(
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
        if (!isGrantedRequiredPermissions) {
            viewModelScope.launch {
                _navigationEventChannel.send(MainDestination.Permission.route)
            }
        }
    }
}