package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.ItemSearchQuery
import com.obrockmole.betterdining.network.ApolloInstance

class SearchRepository {
    private val apolloClient = ApolloInstance.apolloClient

    suspend fun searchItems(query: String): List<ItemSearchQuery.ItemSearch> {
        try {
            val response = apolloClient.query(ItemSearchQuery(name = query)).execute()

            if (response.hasErrors()) {
                throw Exception("GraphQL Error: ${response.errors?.firstOrNull()?.message}")
            } else if (response.exception != null) {
                throw response.exception!!
            }

            val items = response.data!!.itemSearch
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
            throw e
        }
    }
}
