package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.models.UpcomingFavorite
import com.obrockmole.betterdining.repository.UpcomingFavoritesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class UpcomingFavoritesViewModel(private val repository: UpcomingFavoritesRepository) :
    ViewModel() {
    val upcomingFavorites: StateFlow<Result<List<UpcomingFavorite>>> =
        repository.getUpcomingFavoritesFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Result.success(emptyList())
            )
}

class UpcomingFavoritesViewModelFactory(
    private val repository: UpcomingFavoritesRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpcomingFavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UpcomingFavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
