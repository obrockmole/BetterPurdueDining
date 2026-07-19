package com.obrockmole.kmpbetterdining.models

data class UpcomingFavorite(
    val itemId: String,
    val name: String,
    val appearances: List<Appearance>
)