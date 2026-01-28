package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.repository.RenamedItemsRepository

class ItemViewModelFactory(
    private val menuRepository: MenuRepository,
    private val favoritesRepository: FavoritesRepository,
    private val renamedItemsRepository: RenamedItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(menuRepository, favoritesRepository, renamedItemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
