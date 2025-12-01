package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.repository.UserPreferencesRepository
import com.obrockmole.betterdining.database.FavoriteItem
import com.obrockmole.betterdining.repository.FavoritesRepository
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
            for (favoritedItem in importData.data.currentUser.favorites) {
                val itemId = favoritedItem.item.itemId
                val name = favoritedItem.item.name

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
    val data: DataWrapper
) {
    @Serializable
    data class DataWrapper(
        val currentUser: CurrentUser
    )

    @Serializable
    data class CurrentUser(
        val commonName: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val email: String? = null,
        val favorites: List<FavoritedItem>,
        val __typename: String? = null
    )

    @Serializable
    data class FavoritedItem(
        val dateAdded: String? = null,
        val favoriteId: String? = null,
        val item: Item,
        val __typename: String? = null
    )

    @Serializable
    data class Item(
        val id: String? = null,
        val itemId: String,
        val name: String,
        val isNutritionReady: Boolean? = null,
        val isDiscontinued: Boolean? = null,
        val appearances: List<Appearance>? = null,
        val components: List<Component>? = null,
        val traits: List<Trait>? = null,
        val __typename: String? = null
    )

    @Serializable
    data class Appearance(
        val date: String? = null,
        val locationName: String? = null,
        val mealName: String? = null,
        val stationName: String? = null,
        val __typename: String? = null
    )

    @Serializable
    data class Component(
        val itemId: String? = null,
        val name: String? = null,
        val __typename: String? = null
    )

    @Serializable
    data class Trait(
        val name: String? = null,
        val svgIcon: String? = null,
        val svgIconWithoutBackground: String? = null,
        val __typename: String? = null
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
