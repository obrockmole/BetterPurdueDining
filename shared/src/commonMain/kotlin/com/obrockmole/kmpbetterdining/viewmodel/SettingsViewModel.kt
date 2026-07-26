package com.obrockmole.kmpbetterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.kmpbetterdining.models.GitHubRelease
import com.obrockmole.kmpbetterdining.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _latestVersion = MutableStateFlow<String?>(null)
    val latestVersion: StateFlow<String?> = _latestVersion

    private val _latestVersionURL = MutableStateFlow<String?>(null)
    val latestVersionURL: StateFlow<String?> = _latestVersionURL

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
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == SettingsViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
