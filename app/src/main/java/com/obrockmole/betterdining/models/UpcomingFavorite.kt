package com.obrockmole.betterdining.models

import java.time.LocalDate

data class UpcomingFavorite(
    val itemId: String,
    val name: String,
    val appearances: List<Appearance>
)

data class UpcomingAppearance(
    val date: LocalDate,
    val locationName: String,
    val stationName: String,
    val mealName: String
)
