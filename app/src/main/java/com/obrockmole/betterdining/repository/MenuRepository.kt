package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.GetItemDetailsQuery
import com.obrockmole.betterdining.GetLocationMenuQuery
import com.obrockmole.betterdining.network.ApolloInstance
import com.obrockmole.betterdining.utils.Logger

private const val LOG_TAG = "MenuRepository"

class MenuRepository {
    private val apolloClient = ApolloInstance.apolloClient

    suspend fun getDiningCourtMenu(
        courtId: String,
        date: String
    ): GetLocationMenuQuery.DiningCourt {
        try {
            Logger.LogDebug(LOG_TAG, "Getting menu for $courtId on $date")
            val response = apolloClient.query(GetLocationMenuQuery(id = courtId, date = date)).execute()

            if (response.hasErrors()) {
                val errorMessage = "GraphQL Error: ${response.errors?.firstOrNull()?.message}"
                Logger.LogError(LOG_TAG, errorMessage)
                throw Exception(errorMessage)
            } else if (response.exception != null) {
                Logger.LogError(LOG_TAG, "${response.exception?.message}", response.exception)
                throw response.exception!!
            }

            if (response.data?.diningCourt != null) {
                Logger.LogDebug(LOG_TAG, "Successfully got menu for $courtId with ${response.data!!.diningCourt!!.dailyMenu?.meals} meals")
                return response.data!!.diningCourt!!
            } else {
                Logger.LogDebug(LOG_TAG, "No dining court found with ID '$courtId'")
                throw Exception("No dining court found with ID: $courtId")
            }

        } catch (e: Exception) {
            Logger.LogError(LOG_TAG, "An error occurred getting the menu for $courtId", e)
            throw e
        }
    }

    suspend fun getItemDetails(itemId: String): GetItemDetailsQuery.ItemByItemId? {
        try {
            val response = apolloClient.query(GetItemDetailsQuery(id = itemId)).execute()

            if (response.hasErrors()) {
                val errorMessage = "GraphQL Error: ${response.errors?.firstOrNull()?.message}"
                Logger.LogError(LOG_TAG, errorMessage)
                throw Exception(errorMessage)
            } else if (response.exception != null) {
                Logger.LogError(LOG_TAG, "${response.exception?.message}", response.exception)
                throw response.exception!!
            }

            return response.data?.itemByItemId

        } catch (e: Exception) {
            Logger.LogError(LOG_TAG, "An error occurred getting details for $itemId", e)
            throw e
        }
    }
}
