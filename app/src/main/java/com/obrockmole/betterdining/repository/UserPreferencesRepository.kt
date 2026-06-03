package com.obrockmole.betterdining.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.obrockmole.betterdining.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LOG_TAG = "UserPreferencesRepository"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {
    private object PreferencesKeys {
        val DEFAULT_SCREEN = stringPreferencesKey("default_screen")
        val APP_THEME = stringPreferencesKey("app_theme")
        val NAV_STYLE = stringPreferencesKey("nav_style")
        val LOG_LEVEL = stringPreferencesKey("log_level")
    }

    val defaultScreen: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DEFAULT_SCREEN] ?: "Home"
        }

    val appTheme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.APP_THEME] ?: "Material"
        }

    val navStyle: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NAV_STYLE] ?: "Bottom"
        }

    val logLevel: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LOG_LEVEL] ?: "Minimal"
        }

    suspend fun setDefaultScreen(defaultScreen: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_SCREEN] = defaultScreen
        }
        Logger.LogDebug(LOG_TAG, "Setting default screen to: $defaultScreen")
    }

    suspend fun setAppTheme(appTheme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = appTheme
        }
        Logger.LogDebug(LOG_TAG, "Setting app theme to: $appTheme")
    }

    suspend fun setNavStyle(navStyle: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NAV_STYLE] = navStyle
        }
        Logger.LogDebug(LOG_TAG, "Setting navigation style to: $navStyle")
    }

    suspend fun setLogLevel(logLevel: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOG_LEVEL] = logLevel
        }
        Logger.LogDebug(LOG_TAG, "Setting log level to: $logLevel")
    }
}
