package com.obrockmole.kmpbetterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.kmpbetterdining.models.GitHubRelease
import com.obrockmole.kmpbetterdining.repository.SettingsRepository
import com.obrockmole.kmpbetterdining.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val defaultScreen: StateFlow<String> = userPreferencesRepository.defaultScreen
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "Home"
        )

    val appTheme: StateFlow<String> = userPreferencesRepository.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "Material"
        )

    val navStyle: StateFlow<String> = userPreferencesRepository.navStyle
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "Bottom"
        )

    val logAmount: StateFlow<String> = userPreferencesRepository.logAmount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "Minimal"
        )

    private val _latestVersion = MutableStateFlow<String?>(null)
    val latestVersion: StateFlow<String?> = _latestVersion

    private val _latestVersionURL = MutableStateFlow<String?>(null)
    val latestVersionURL: StateFlow<String?> = _latestVersionURL

    fun setDefaultScreen(defaultScreen: String) {
        viewModelScope.launch {
            userPreferencesRepository.setDefaultScreen(defaultScreen)
        }
    }

    fun setAppTheme(appTheme: String) {
        viewModelScope.launch {
            userPreferencesRepository.setAppTheme(appTheme)
        }
    }

    fun setNavStyle(navStyle: String) {
        viewModelScope.launch {
            userPreferencesRepository.setNavStyle(navStyle)
        }
    }

    fun setLogAmount(logAmount: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLogAmount(logAmount)
        }
    }

    suspend fun getLatestVersion() {
        _latestVersion.value = null
        _latestVersionURL.value = null
        try {
            val latestRelease: GitHubRelease? = settingsRepository.getLatestRelease()
            if (latestRelease != null) {
                _latestVersion.value = latestRelease.tag_name.removePrefix("v")
                _latestVersionURL.value = latestRelease.html_url
            }
        } catch (e: Exception) {
            _latestVersion.value = null
            _latestVersionURL.value = null
        }
    }
}

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == SettingsViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
