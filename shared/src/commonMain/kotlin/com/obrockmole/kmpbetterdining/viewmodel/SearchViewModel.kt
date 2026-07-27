package com.obrockmole.kmpbetterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.kmpbetterdining.repository.RenamedItemsRepository
import com.obrockmole.kmpbetterdining.repository.SearchRepository
import kotlin.reflect.KClass

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val renamedItemsRepository: RenamedItemsRepository
) : ViewModel() {
    suspend fun searchItems(query: String): List<SearchItemDisplay> {
        val searchResults = searchRepository.searchItems(query)
        return searchResults.map { item ->
            val renamedItem = renamedItemsRepository.getRenamedItem(item.itemId)
            SearchItemDisplay(
                originalItem = item,
                displayName = renamedItem?.customName ?: item.name
            )
        }
    }
}

class SearchViewModelFactory(
    private val searchRepository: SearchRepository,
    private val renamedItemsRepository: RenamedItemsRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == SearchViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchRepository, renamedItemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
