package com.obrockmole.kmpbetterdining.viewmodel

import com.obrockmole.kmpbetterdining.GetLocationMenuQuery
import com.obrockmole.kmpbetterdining.ItemSearchQuery

data class DiningCourtMenuDisplay(
    val name: String,
    val courtId: String,
    val meals: List<MealDisplay>
)

data class MealDisplay(
    val name: String,
    val stations: List<StationDisplay>,
    val startTime: String? = null,
    val endTime: String? = null
)

data class StationDisplay(
    val name: String,
    val items: List<ItemDisplay>
)

data class ItemDisplay(
    val originalItem: GetLocationMenuQuery.Item,
    val displayName: String
)

data class SearchItemDisplay(
    val originalItem: ItemSearchQuery.ItemSearch,
    val displayName: String
)


