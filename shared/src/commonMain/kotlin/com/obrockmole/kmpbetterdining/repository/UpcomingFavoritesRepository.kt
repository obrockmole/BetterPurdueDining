package com.obrockmole.kmpbetterdining.repository

import com.obrockmole.betterdining.network.buildMultiItemQuery
import com.obrockmole.kmpbetterdining.database.GetAllWithCustomNames
import com.obrockmole.kmpbetterdining.models.GraphQLRequest
import com.obrockmole.kmpbetterdining.models.UpcomingFavorite
import com.obrockmole.kmpbetterdining.network.DiningApi
import com.obrockmole.kmpbetterdining.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

private const val LOG_TAG = "UpcomingFavoritesRepository"

class UpcomingFavoritesRepository(private val favorites: StateFlow<List<GetAllWithCustomNames>>) {
    fun getUpcomingFavoritesFlow(): Flow<Result<List<UpcomingFavorite>>> {
        return favorites.map { favorites ->
            try {
                if (favorites.isEmpty()) {
                    return@map Result.success(emptyList())
                }
                val favoriteIds = favorites.map { it.itemId }

                val query = buildMultiItemQuery(favoriteIds)
                val request = GraphQLRequest(query = query, variables = emptyMap())
                val response = DiningApi.getMultipleItems(request)

                val upcomingFavorites =
                    response.data.values.mapNotNull { itemDetails ->
                        val favorite = favorites.find { it.itemId == itemDetails.itemId }
                        favorite?.let {
                            UpcomingFavorite(
                                itemId = itemDetails.itemId,
                                name = it.name,
                                appearances = itemDetails.appearances
                            )
                        }
                    }

                Logger.LogDebug(LOG_TAG, "Successfully fetched ${upcomingFavorites.size} upcoming favorites")
                Result.success(upcomingFavorites)
            } catch (e: Exception) {
                Logger.LogError(LOG_TAG, "Unknown error occurred while fetching upcoming favorites")
                Result.failure(e)
            }
        }
    }
}
