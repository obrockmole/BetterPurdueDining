package com.obrockmole.betterdining.models

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

data class Trait(
    val name: String,
    val type: String? = null,
    val svgIcon: String?,
    val svgIconWithoutBackground: String?
)

data class Component(
    val name: String,
    val itemId: String,
    val traits: List<Trait>,
    val isFlaggedForCurrentUser: Boolean? = null,
    val isHiddenForCurrentUser: Boolean? = null,
    val isNutritionReady: Boolean? = null
)

data class Appearance(
    val locationName: String,
    val stationName: String,
    val mealName: String,
    val date: String
)