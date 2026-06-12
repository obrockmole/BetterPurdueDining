package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.FavoriteItemDao
import com.obrockmole.betterdining.models.GraphQLRequest
import com.obrockmole.betterdining.models.UpcomingFavorite
import com.obrockmole.betterdining.network.DiningApi
import com.obrockmole.betterdining.network.RetrofitInstance
import com.obrockmole.betterdining.network.buildMultiItemQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.obrockmole.betterdining.utils.Logger

private const val LOG_TAG = "UpcomingFavoritesRepository"

class UpcomingFavoritesRepository(
    private val favoriteItemDao: FavoriteItemDao,
) {
    private val diningApi: DiningApi = RetrofitInstance.diningApi

    fun getUpcomingFavoritesFlow(): Flow<Result<List<UpcomingFavorite>>> {
        return favoriteItemDao.getAllWithCustomNames().map { favorites ->
            try {
                if (favorites.isEmpty()) {
                    return@map Result.success(emptyList())
                }
                val favoriteIds = favorites.map { it.itemId }

                val query = buildMultiItemQuery(favoriteIds)
                val request = GraphQLRequest(query = query, variables = emptyMap<String, Any>())
                val response = diningApi.getMultipleItems(request)

                if (response.isSuccessful && response.body() != null) {
                    val upcomingFavorites =
                        response.body()!!.data.values.mapNotNull { itemDetails ->
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
                } else {
                    Logger.LogError(LOG_TAG, "Failed to fetch upcoming favorites: ${response.errorBody()?.string()}")
                    Result.failure(Exception("Failed to fetch upcoming favorites"))
                }
            } catch (e: Exception) {
                Logger.LogError(LOG_TAG, "Unknown error occurred while fetching upcoming favorites", e)
                Result.failure(e)
            }
        }
    }
}
