package com.obrockmole.betterdining.viewmodel

import com.obrockmole.betterdining.ItemSearchQuery

data class SearchResultDisplay(
    val originalItem: ItemSearchQuery.ItemSearch,
    val displayName: String
)

