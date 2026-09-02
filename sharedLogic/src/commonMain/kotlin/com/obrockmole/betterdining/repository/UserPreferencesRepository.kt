package com.obrockmole.betterdining.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.obrockmole.betterdining.utils.Logger
import com.obrockmole.betterdining.utils.Platform
import com.obrockmole.betterdining.utils.PlatformType
import com.obrockmole.betterdining.utils.getPlatform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LOG_TAG = "UserPreferencesRepository"

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val DEFAULT_SCREEN = stringPreferencesKey("default_screen")
        val APP_THEME = stringPreferencesKey("app_theme")
        val NAV_STYLE = stringPreferencesKey("nav_style")
        val LOG_AMOUNT = stringPreferencesKey("log_amount")
    }

    val defaultScreen: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DEFAULT_SCREEN] ?: "Home"
        }

    val appTheme: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.APP_THEME] ?: if (getPlatform().type == PlatformType.ANDROID) "Material" else "Dark"
        }

    val navStyle: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NAV_STYLE] ?: "Bottom"
        }

    val logAmount: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LOG_AMOUNT] ?: "Minimal"
        }

    suspend fun setDefaultScreen(defaultScreen: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_SCREEN] = defaultScreen
        }
        Logger.LogDebug(LOG_TAG, "Setting default screen to: $defaultScreen")
    }

    suspend fun setAppTheme(appTheme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = appTheme
        }
        Logger.LogDebug(LOG_TAG, "Setting app theme to: $appTheme")
    }

    suspend fun setNavStyle(navStyle: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NAV_STYLE] = navStyle
        }
        Logger.LogDebug(LOG_TAG, "Setting navigation style to: $navStyle")
    }

    suspend fun setLogAmount(logAmount: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOG_AMOUNT] = logAmount
        }
        Logger.LogDebug(LOG_TAG, "Setting log amount to: $logAmount")
    }
}
