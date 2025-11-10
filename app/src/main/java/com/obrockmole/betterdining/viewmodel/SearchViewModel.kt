package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obrockmole.betterdining.repository.GroupedSearchResult
import com.obrockmole.betterdining.repository.SearchRepository

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {
    suspend fun searchUpcoming(query: String): Result<List<GroupedSearchResult>> {
        return searchRepository.searchUpcoming(query)
    }
}

class SearchViewModelFactory(private val searchRepository: SearchRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
