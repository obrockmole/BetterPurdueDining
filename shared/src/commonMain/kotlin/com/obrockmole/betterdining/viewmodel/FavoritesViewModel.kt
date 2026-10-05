package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.betterdining.database.GetAllWithCustomNames
import com.obrockmole.betterdining.repository.FavoritesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.reflect.KClass

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    val favorites: StateFlow<List<GetAllWithCustomNames>> =
        favoritesRepository.getAllWithCustomNames()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}

class FavoritesViewModelFactory(
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == FavoritesViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(favoritesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}