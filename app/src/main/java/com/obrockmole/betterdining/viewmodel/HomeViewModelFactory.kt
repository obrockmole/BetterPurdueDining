package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obrockmole.betterdining.repository.StartLocationsRepository

class HomeViewModelFactory(
    private val startLocationsRepository: StartLocationsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(startLocationsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
