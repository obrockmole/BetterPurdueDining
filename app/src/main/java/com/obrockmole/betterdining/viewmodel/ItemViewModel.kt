package com.obrockmole.betterdining.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.GetItemDetailsQuery
import com.obrockmole.betterdining.database.FavoriteItem
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.MenuRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed interface ItemUiState {
    data class Success(val item: GetItemDetailsQuery.ItemByItemId) : ItemUiState
    data object Error : ItemUiState
    data object Loading : ItemUiState
}

class ItemViewModel(
    private val menuRepository: MenuRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set

    var isFavorite by mutableStateOf(false)
        private set

    fun getItem(itemId: String) {
        viewModelScope.launch {
            itemUiState = ItemUiState.Loading
            try {
                val result = menuRepository.getItemDetails(itemId)
                itemUiState = if (result != null) {
                    ItemUiState.Success(result)
                } else {
                    ItemUiState.Error
                }
                isFavorite = favoritesRepository.isFavorite(itemId)

            } catch (e: Exception) {
                itemUiState = ItemUiState.Error
            }
        }
    }

    fun toggleFavorite(item: GetItemDetailsQuery.ItemByItemId) {
        viewModelScope.launch {
            val date = OffsetDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val favoriteItem = FavoriteItem(name = item.name, itemId = item.itemId, dateAdded = date)
            if (isFavorite) {
                favoritesRepository.removeFavorite(favoriteItem)
            } else {
                favoritesRepository.addFavorite(favoriteItem)
            }
            isFavorite = !isFavorite
        }
    }
}
