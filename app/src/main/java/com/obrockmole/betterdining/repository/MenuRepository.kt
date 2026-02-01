package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.GetItemDetailsQuery
import com.obrockmole.betterdining.GetLocationMenuQuery
import com.obrockmole.betterdining.network.ApolloInstance

class MenuRepository {
    private val apolloClient = ApolloInstance.apolloClient

    suspend fun getDiningCourtMenu(
        courtId: String,
        date: String
    ): GetLocationMenuQuery.DiningCourt {
        try {
            val response = apolloClient.query(GetLocationMenuQuery(id = courtId, date = date)).execute()

            if (response.hasErrors()) {
                throw Exception("GraphQL Error: ${response.errors?.firstOrNull()?.message}")
            } else if (response.exception != null) {
                throw response.exception!!
            }

            return response.data?.diningCourt ?: throw Exception("No dining court found with ID: $courtId")

        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getItemDetails(itemId: String): GetItemDetailsQuery.ItemByItemId? {
        try {
            val response = apolloClient.query(GetItemDetailsQuery(id = itemId)).execute()

            if (response.hasErrors()) {
                throw Exception("GraphQL Error: ${response.errors?.firstOrNull()?.message}")
            } else if (response.exception != null) {
                throw response.exception!!
            }

            return response.data?.itemByItemId

        } catch (e: Exception) {
            throw e
        }
    }
}
