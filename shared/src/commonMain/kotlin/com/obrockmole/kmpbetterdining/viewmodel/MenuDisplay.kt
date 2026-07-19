package com.obrockmole.kmpbetterdining.viewmodel

import com.obrockmole.kmpbetterdining.GetLocationMenuQuery

data class MenuItemDisplay(
    val originalItem: GetLocationMenuQuery.Item,
    val displayName: String
)

data class StationDisplay(
    val name: String,
    val items: List<MenuItemDisplay>
)

data class MealDisplay(
    val name: String,
    val stations: List<StationDisplay>,
    val startTime: String? = null,
    val endTime: String? = null
)

data class DiningCourtMenuDisplay(
    val name: String,
    val courtId: String,
    val meals: List<MealDisplay>
)

