package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obrockmole.betterdining.models.UpcomingFavorite
import com.obrockmole.betterdining.repository.UpcomingFavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UpcomingFavoritesViewModel(private val repository: UpcomingFavoritesRepository) : ViewModel() {

    private val _upcomingFavorites = MutableStateFlow<Result<List<UpcomingFavorite>>>(Result.success(emptyList()))
    val upcomingFavorites: StateFlow<Result<List<UpcomingFavorite>>> = _upcomingFavorites

    init {
        fetchUpcomingFavorites()
    }

    fun fetchUpcomingFavorites() {
        viewModelScope.launch {
            _upcomingFavorites.value = repository.getUpcomingFavorites()
        }
    }
}

class UpcomingFavoritesViewModelFactory(private val repository: UpcomingFavoritesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpcomingFavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UpcomingFavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
