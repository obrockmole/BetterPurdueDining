package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.GetStartLocationsQuery
import com.obrockmole.betterdining.network.ApolloInstance

class StartLocationsRepository {
    private val apolloClient = ApolloInstance.apolloClient

    suspend fun getStartLocations(date: String): List<GetStartLocationsQuery.DiningCourtCategory>? {
        try {
            val response = apolloClient.query(GetStartLocationsQuery(date)).execute()

            if (response.hasErrors()) {
                throw Exception("GraphQL Error: ${response.errors?.firstOrNull()?.message}")
            } else if (response.exception != null) {
                throw response.exception!!
            }

            return response.data?.diningCourtCategories

        } catch (e: Exception) {
            throw e
        }
    }
}
