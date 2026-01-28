package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obrockmole.betterdining.repository.RenamedItemsRepository
import com.obrockmole.betterdining.repository.SearchRepository

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val renamedItemsRepository: RenamedItemsRepository
) : ViewModel() {
    suspend fun searchItems(query: String): List<SearchResultDisplay> {
        val searchResults = searchRepository.searchItems(query)
        return searchResults.map { item ->
            val renamedItem = renamedItemsRepository.getRenamedItem(item.itemId)
            SearchResultDisplay(
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
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchRepository, renamedItemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
