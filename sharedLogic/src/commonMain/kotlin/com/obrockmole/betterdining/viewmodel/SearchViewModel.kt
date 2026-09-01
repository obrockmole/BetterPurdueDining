package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.betterdining.graphql.ItemSearchQuery
import com.obrockmole.betterdining.repository.SearchRepository
import kotlin.reflect.KClass

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    suspend fun searchItems(query: String): List<ItemSearchQuery.ItemSearch> {
        val searchResults = searchRepository.searchItems(query)
        return searchResults
    }
}

class SearchViewModelFactory(
    private val searchRepository: SearchRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == SearchViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
