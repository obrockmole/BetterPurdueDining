package com.obrockmole.betterdining.models

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

