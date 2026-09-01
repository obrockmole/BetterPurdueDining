package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.betterdining.database.GetAllWithCustomNames
import com.obrockmole.betterdining.models.UpcomingFavorite
import com.obrockmole.betterdining.repository.UpcomingFavoritesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.reflect.KClass

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
    private val favorites: StateFlow<List<GetAllWithCustomNames>>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == UpcomingFavoritesViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return UpcomingFavoritesViewModel(UpcomingFavoritesRepository(favorites)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
