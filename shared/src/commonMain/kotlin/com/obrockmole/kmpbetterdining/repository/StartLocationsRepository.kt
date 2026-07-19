package com.obrockmole.kmpbetterdining.repository

import com.obrockmole.kmpbetterdining.GetStartLocationsQuery
import com.obrockmole.kmpbetterdining.network.ApolloInstance
import com.obrockmole.kmpbetterdining.utils.Logger

private const val LOG_TAG = "StartLocationsRepository"

class StartLocationsRepository {
    private val apolloClient = ApolloInstance.apolloClient

    suspend fun getStartLocations(date: String): List<GetStartLocationsQuery.DiningCourtCategory>? {
        try {
            Logger.LogDebug(LOG_TAG, "Getting start locations for $date")
            val response = apolloClient.query(GetStartLocationsQuery(date)).execute()

            if (response.hasErrors()) {
                val errorMessage = "GraphQL Error: ${response.errors?.firstOrNull()?.message}"
                Logger.LogError(LOG_TAG, errorMessage)
                throw Exception(errorMessage)
            } else if (response.exception != null) {
                Logger.LogError(LOG_TAG, "${response.exception?.message}")
                throw response.exception!!
            }

            var numDiningCourts = 0
            for (category in response.data?.diningCourtCategories ?: emptyList()) {
                numDiningCourts += category.diningCourts.size
            }

            Logger.LogDebug(LOG_TAG, "Got $numDiningCourts start locations across ${response.data?.diningCourtCategories?.size ?: 0} categories")
            return response.data?.diningCourtCategories

        } catch (e: Exception) {
            throw e
        }
    }
}
