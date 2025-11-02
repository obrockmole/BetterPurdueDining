package com.obrockmole.betterdining.models

data class GraphQLRequest(
    val operationName: String? = null,
    val variables: Any,
    val query: String
)

data class Variables(
    val name: String,
    val date: String
)

data class MenuResponse(
    val data: Data
)

data class Data(
    val diningCourtByName: DiningCourtByName
)

data class DiningCourtByName(
    val name: String,
    val formalName: String,
    val id: String,
    val lineLength: String?,
    val dailyMenu: DailyMenu
)

data class DailyMenu(
    val notes: String?,
    val meals: List<Meal>
)

data class Meal(
    val name: String,
    val notes: String?,
    val status: String,
    val startTime: String?,
    val endTime: String?,
    val stations: List<Station>
)

data class Station(
    val name: String,
    val id: String,
    val items: List<ItemWrapper>
)

data class ItemWrapper(
    val specialName: String?,
    val itemMenuId: String,
    val hasComponents: Boolean,
    val item: Item
)

data class Item(
    val name: String,
    val itemId: String,
    val traits: List<Trait>,
    val components: List<Component>? = null
)

data class ItemVariables(
    val id: String
)
