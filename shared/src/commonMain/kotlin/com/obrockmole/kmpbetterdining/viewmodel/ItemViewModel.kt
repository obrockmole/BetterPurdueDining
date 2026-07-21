package com.obrockmole.kmpbetterdining.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.kmpbetterdining.GetItemDetailsQuery
import com.obrockmole.kmpbetterdining.database.FavoriteItem
import com.obrockmole.kmpbetterdining.database.RenamedItem
import com.obrockmole.kmpbetterdining.repository.FavoritesRepository
import com.obrockmole.kmpbetterdining.repository.MenuRepository
import com.obrockmole.kmpbetterdining.repository.RenamedItemsRepository
import com.obrockmole.kmpbetterdining.utils.DateTime
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

sealed interface ItemUiState {
    data class Success(val item: GetItemDetailsQuery.ItemByItemId) : ItemUiState
    data class Error(val message: String) : ItemUiState
    data object Loading : ItemUiState
}

class ItemViewModel(
    private val menuRepository: MenuRepository,
    private val favoritesRepository: FavoritesRepository,
    private val renamedItemsRepository: RenamedItemsRepository
) : ViewModel() {

    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set

    var isFavorite by mutableStateOf(false)
        private set

    var isRenamed by mutableStateOf(false)
        private set

    var renamedName by mutableStateOf("")
        private set

    fun getItem(itemId: String) {
        viewModelScope.launch {
            itemUiState = ItemUiState.Loading
            try {
                val result = menuRepository.getItemDetails(itemId)
                val renamedItem = renamedItemsRepository.getRenamedItem(itemId)
                if (renamedItem != null) {
                    isRenamed = true
                    renamedName = renamedItem.customName
                }
                itemUiState = if (result != null) {
                    ItemUiState.Success(result)
                } else {
                    ItemUiState.Error("Item not found")
                }
                isFavorite = favoritesRepository.isFavorite(itemId)

            } catch (e: Exception) {
                itemUiState = ItemUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun renameItem(itemId: String, customName: String) {
        viewModelScope.launch {
            val renamedItem = RenamedItem(itemId, customName)
            renamedItemsRepository.insert(renamedItem)
            isRenamed = true
            renamedName = customName
        }
    }

    fun toggleFavorite(item: GetItemDetailsQuery.ItemByItemId) {
        viewModelScope.launch {
            val date = DateTime.getLocalDateTime()
            val favoriteItem = FavoriteItem(item.itemId, item.name, date.toString())

            if (isFavorite) {
                favoritesRepository.removeFavorite(favoriteItem)
            } else {
                favoritesRepository.addFavorite(favoriteItem)
            }

            isFavorite = !isFavorite
        }
    }
}

class ItemViewModelFactory(
    private val menuRepository: MenuRepository,
    private val favoritesRepository: FavoritesRepository,
    private val renamedItemsRepository: RenamedItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == ItemViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(menuRepository, favoritesRepository, renamedItemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}