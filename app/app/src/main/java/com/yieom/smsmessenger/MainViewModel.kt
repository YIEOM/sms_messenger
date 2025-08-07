package com.yieom.smsmessenger

import android.Manifest
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
): ViewModel() {
    init {
        Timber.d("##init")
    }
    var hasPermissions: Boolean = false
    fun hasPermissions(): Boolean {
        return hasPermissions
    }

    val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.SEND_SMS,
    )
}