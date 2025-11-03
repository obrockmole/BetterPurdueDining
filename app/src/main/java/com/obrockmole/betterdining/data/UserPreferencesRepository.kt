package com.obrockmole.betterdining.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {
    private object PreferencesKeys {
        val DEFAULT_SCREEN = stringPreferencesKey("default_screen")
    }

    val defaultScreen: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DEFAULT_SCREEN] ?: "Home"
        }

    suspend fun setDefaultScreen(defaultScreen: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_SCREEN] = defaultScreen
        }
    }
}
