package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.models.Appearance
import com.obrockmole.betterdining.network.DiningApi
import com.obrockmole.betterdining.network.RetrofitInstance
import com.obrockmole.betterdining.ui.screens.SearchResultItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class GroupedSearchResult(
    val itemId: String,
    val name: String,
    val appearances: List<SearchResultItem>
)

class SearchRepository {
    private val diningApi: DiningApi = RetrofitInstance.api

    suspend fun searchUpcoming(query: String): Result<List<GroupedSearchResult>> {
        return try {
            val response = diningApi.searchUpcoming(query)

            if (response.isSuccessful && response.body() != null) {
                val searchResponse = response.body()!!
                val allAppearances = mutableListOf<SearchResultItem>()

                searchResponse.results.forEach { searchResult ->
                    searchResult.itemSchedule.itemAppearances.forEach { itemAppearance ->
                        allAppearances.add(
                            SearchResultItem(
                                itemId = searchResult.id,
                                name = searchResult.name,
                                appearance = Appearance(
                                    mealName = itemAppearance.meal,
                                    locationName = itemAppearance.location,
                                    stationName = itemAppearance.station,
                                    date = itemAppearance.date
                                )
                            )
                        )
                    }
                }

                val groupedResults = allAppearances.groupBy { it.name }
                    .map { (name, items) ->
                        val mergedAppearances = items
                            .groupBy {
                                Triple(
                                    it.appearance.locationName,
                                    it.appearance.mealName,
                                    it.appearance.date
                                )
                            }
                            .map { (_, groupedItems) ->
                                val first = groupedItems.first()
                                val combinedStations =
                                    groupedItems.map { it.appearance.stationName }.distinct()
                                        .joinToString(", ")
                                SearchResultItem(
                                    itemId = first.itemId,
                                    name = first.name,
                                    appearance = first.appearance.copy(stationName = combinedStations)
                                )
                            }
                            .sortedBy {
                                LocalDate.parse(
                                    it.appearance.date,
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                )
                            }

                        GroupedSearchResult(
                            itemId = items.first().itemId,
                            name = name,
                            appearances = mergedAppearances
                        )
                    }
                    .sortedBy { it.name }

                Result.success(groupedResults)
            } else {
                Result.failure(Exception("Failed to search: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
