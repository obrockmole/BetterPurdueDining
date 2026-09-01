package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.betterdining.database.FavoriteItem
import com.obrockmole.betterdining.database.RenamedItem
import com.obrockmole.betterdining.graphql.GetItemDetailsQuery
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.repository.RenamedItemsRepository
import com.obrockmole.betterdining.utils.DateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _itemUiState = MutableStateFlow<ItemUiState>(ItemUiState.Loading)
    val itemUiState: StateFlow<ItemUiState> = _itemUiState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _isRenamed = MutableStateFlow(false)
    val isRenamed: StateFlow<Boolean> = _isRenamed

    private val _renamedName = MutableStateFlow("")
    val renamedName: StateFlow<String> = _renamedName

    fun getItem(itemId: String) {
        viewModelScope.launch {
            _itemUiState.value = ItemUiState.Loading
            try {
                val result = menuRepository.getItemDetails(itemId)
                val renamedItem = renamedItemsRepository.getRenamedItem(itemId)
                if (renamedItem != null) {
                    _isRenamed.value = true
                    _renamedName.value = renamedItem.customName
                }
                _itemUiState.value = if (result != null) {
                    ItemUiState.Success(result)
                } else {
                    ItemUiState.Error("Item not found")
                }
                _isFavorite.value = favoritesRepository.isFavorite(itemId)

            } catch (e: Exception) {
                _itemUiState.value = ItemUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun renameItem(itemId: String, customName: String) {
        viewModelScope.launch {
            val renamedItem = RenamedItem(itemId, customName)
            renamedItemsRepository.insert(renamedItem)
            _isRenamed.value = true
            _renamedName.value = customName
        }
    }

    fun toggleFavorite(item: GetItemDetailsQuery.ItemByItemId) {
        viewModelScope.launch {
            val date = DateTime.getLocalDateTime()
            val favoriteItem = FavoriteItem(item.itemId, item.name, date.toString())

            if (_isFavorite.value) {
                favoritesRepository.removeFavorite(favoriteItem)
            } else {
                favoritesRepository.addFavorite(favoriteItem)
            }

            _isFavorite.value = !_isFavorite.value
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