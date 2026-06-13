package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.database.FavoriteItem
import com.obrockmole.betterdining.models.GitHubRelease
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.SettingsRepository
import com.obrockmole.betterdining.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.json.Json as KotlinJson

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val favoritesRepository: FavoritesRepository
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

    val logLevel: StateFlow<String> = userPreferencesRepository.logLevel
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

    fun setLogLevel(logLevel: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLogLevel(logLevel)
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

    suspend fun importFavorites(jsonString: String): Result<Int> {
        return try {
            val json = KotlinJson { ignoreUnknownKeys = true }
            val importData = json.decodeFromString<ImportFavoritesData>(jsonString)

            var addedCount = 0
            for (favoriteItem in importData.Favorite) {
                val itemId = favoriteItem.ItemId
                val name = favoriteItem.ItemName
                val dateAdded = favoriteItem.DateAdded

                if (!favoritesRepository.isFavorite(itemId)) {
                    favoritesRepository.addFavorite(FavoriteItem(itemId, name, dateAdded))
                    addedCount++
                } else {
                    val favorite = favoritesRepository.getFavorite(itemId)
                    if (favorite != null) {
                        val date1 = OffsetDateTime.parse(
                            favorite.dateAdded,
                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        )
                        val date2 =
                            OffsetDateTime.parse(dateAdded, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

                        if (date2.isBefore(date1)) {
                            favoritesRepository.addFavorite(FavoriteItem(itemId, name, dateAdded))
                            addedCount++
                        }
                    }
                }
            }

            Result.success(addedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class ImportFavoritesData(
    val Favorite: List<FavoriteItem>
) {
    @Serializable
    data class FavoriteItem(
        val FavoriteId: String,
        val ItemId: String,
        val ItemName: String,
        val DateAdded: String,
        val IsVegetarian: Boolean,
        val NutritionReady: Boolean,
        val IsDiscontinued: Boolean,
        val Allergens: List<Allergen>?
    )

    @Serializable
    data class Allergen(
        val Name: String,
        val Value: Boolean
    )
}

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository, userPreferencesRepository, favoritesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
