package com.obrockmole.betterdining.models

data class ItemDetailsResponse(
    val data: ItemData
)

data class ItemData(
    val itemByItemId: ItemDetails
)

data class ItemDetails(
    val name: String,
    val itemId: String,
    val ingredients: String?,
    val nutritionFacts: List<NutritionFact>,
    val traits: List<Trait>,
    val components: List<Component>,
    val appearances: List<Appearance>
)

data class NutritionFact(
    val name: String,
    val value: Double,
    val label: String,
    val dailyValueLabel: String?
)

data class Appearance(
    val locationName: String,
    val stationName: String,
    val mealName: String,
    val date: String
)
