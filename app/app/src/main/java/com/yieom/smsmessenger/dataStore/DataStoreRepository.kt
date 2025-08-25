package com.yieom.smsmessenger.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
        }

        suspend fun saveSheetUrl(url: String) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SHEET_URL] = url
            }
        }

        suspend fun getUrl(): String =
            dataStore.data
                .map { preferences ->
                    preferences[PreferencesKeys.SHEET_URL] ?: ""
                }.first()
    }
