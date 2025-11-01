package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obrockmole.betterdining.repository.FavoritesRepository

class FavoritesViewModelFactory(
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(favoritesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

