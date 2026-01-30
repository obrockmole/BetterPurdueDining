package com.obrockmole.betterdining.viewmodel

import com.obrockmole.betterdining.GetLocationMenuQuery

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
    val meals: List<MealDisplay>
)

