package com.obrockmole.betterdining.models

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("Results")
    val results: List<SearchResult>
)

data class SearchResult(
    @SerializedName("ID")
    val id: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("ItemSchedule")
    val itemSchedule: ItemSchedule
)

data class ItemSchedule(
    @SerializedName("ItemAppearance")
    val itemAppearances: List<ItemAppearance>
)

data class ItemAppearance(
    @SerializedName("Date")
    val date: String,
    @SerializedName("Location")
    val location: String,
    @SerializedName("Meal")
    val meal: String,
    @SerializedName("Station")
    val station: String
)
