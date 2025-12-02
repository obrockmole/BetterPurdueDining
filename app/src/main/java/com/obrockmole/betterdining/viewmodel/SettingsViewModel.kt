package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.database.FavoriteItem
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json as KotlinJson

class SettingsViewModel(
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

    suspend fun importFavorites(jsonString: String): Result<Int> {
        return try {
            val json = KotlinJson { ignoreUnknownKeys = true }
            val importData = json.decodeFromString<ImportFavoritesData>(jsonString)

            var addedCount = 0
            for (favoriteItem in importData.Favorite) {
                val itemId = favoriteItem.ItemId
                val name = favoriteItem.ItemName

                if (!favoritesRepository.isFavorite(itemId)) {
                    favoritesRepository.addFavorite(FavoriteItem(itemId, name))
                    addedCount++
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
    private val userPreferencesRepository: UserPreferencesRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(userPreferencesRepository, favoritesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
