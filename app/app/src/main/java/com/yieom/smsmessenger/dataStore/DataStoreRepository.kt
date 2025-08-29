package com.yieom.smsmessenger.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DataStoreRepository
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private object PreferencesKeys {
            val SHEET_URL = stringPreferencesKey("sheet_url")
            val IS_PERMISSION_HANDLED = booleanPreferencesKey("is_permission_handled")
        }

        suspend fun saveSheetUrl(url: String) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SHEET_URL] = url
            }
        }

        fun getSheetUrl(): Flow<String> =
            dataStore.data
                .map { preferences ->
                    preferences[PreferencesKeys.SHEET_URL] ?: ""
                }

        suspend fun saveIsPermissionHandled(isHandled: Boolean) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_PERMISSION_HANDLED] = isHandled
            }
        }

        fun getIsPermissionHandled(): Flow<Boolean> =
            dataStore.data
                .map { preferences ->
                    preferences[PreferencesKeys.IS_PERMISSION_HANDLED] ?: false
                }
    }
