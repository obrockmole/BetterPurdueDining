package com.obrockmole.betterdining.models

data class UpcomingFavorite(
    val itemId: String,
    val name: String,
    val appearances: List<Appearance>
)