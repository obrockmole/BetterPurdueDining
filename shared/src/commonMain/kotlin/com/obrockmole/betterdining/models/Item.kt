package com.obrockmole.betterdining.models

import kotlinx.serialization.Serializable

@Serializable
data class ItemDetails(
    val name: String,
    val itemId: String,
    val ingredients: String? = null,
    val nutritionFacts: List<NutritionFact>? = null,
    val traits: List<Trait>? = null,
    val components: List<Component>? = null,
    val appearances: List<Appearance>
)

@Serializable
data class NutritionFact(
    val name: String,
    val value: Double,
    val label: String,
    val dailyValueLabel: String?
)

@Serializable
data class Trait(
    val name: String,
    val type: String? = null,
    val svgIcon: String?,
    val svgIconWithoutBackground: String?
)

@Serializable
data class Component(
    val name: String,
    val itemId: String,
    val traits: List<Trait>,
    val isFlaggedForCurrentUser: Boolean? = null,
    val isHiddenForCurrentUser: Boolean? = null,
    val isNutritionReady: Boolean? = null
)

@Serializable
data class Appearance(
    val locationName: String,
    val stationName: String? = null,
    val mealName: String,
    val date: String
)