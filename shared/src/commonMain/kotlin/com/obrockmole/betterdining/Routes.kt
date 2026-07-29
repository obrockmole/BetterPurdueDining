package com.obrockmole.betterdining

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object FavoritesRoute

@Serializable
data object SettingsRoute

@Serializable
data class LocationRoute(
    val locationId: String,
    val locationName: String,
    val initialMealName: String? = null,
    val initialDate: String? = null,
    val initialItemName: String? = null
)

@Serializable
data class ItemRoute(
    val itemId: String,
    val itemName: String
)

@Serializable
data object SearchRoute

@Serializable
data object DefaultScreenRoute

@Serializable
data object ThemeRoute

@Serializable
data object NavStyleRoute

@Serializable
data object LicensesRoute

@Serializable
data object LogAmountRoute