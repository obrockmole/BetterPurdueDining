package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.ItemSearchQuery
import com.obrockmole.betterdining.network.ApolloInstance
import com.obrockmole.betterdining.utils.Logger

private const val LOG_TAG = "SearchRepository"

class SearchRepository {
    private val apolloClient = ApolloInstance.apolloClient

    suspend fun searchItems(query: String): List<ItemSearchQuery.ItemSearch> {
        try {
            Logger.LogDebug(LOG_TAG, "Searching with query '$query'")
            val response = apolloClient.query(ItemSearchQuery(name = query)).execute()

            if (response.hasErrors()) {
                val errorMessage = "GraphQL Error: ${response.errors?.firstOrNull()?.message}"
                Logger.LogError(LOG_TAG, errorMessage)
                throw Exception(errorMessage)
            } else if (response.exception != null) {
                Logger.LogError(LOG_TAG, "${response.exception?.message}", response.exception)
                throw response.exception!!
            }

            val items = response.data!!.itemSearch
            Logger.LogDebug(LOG_TAG, "Search successfully got ${items.size} items")

            val itemsWithTrimmedNames = items.map {
                it.copy(name = it.name.trim())
            }

            val groupedByName = itemsWithTrimmedNames.groupBy { it.name.lowercase() }
                .map { (_, duplicates) ->
                    ItemSearchQuery.ItemSearch(
                        name = duplicates.first().name,
                        itemId = duplicates.first().itemId,
                        appearances = duplicates.flatMap { it.appearances }
                    )
                }

            val itemsSorted = groupedByName.sortedWith(
                compareBy<ItemSearchQuery.ItemSearch> { it.appearances.isEmpty() }
                    .thenBy { it.name }
            )

            return itemsSorted

        } catch (e: Exception) {
            Logger.LogError(LOG_TAG, "Search failed with query '$query'", e)
            throw e
        }
    }
}
