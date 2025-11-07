package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.FavoriteItemDao
import com.obrockmole.betterdining.models.GraphQLRequest
import com.obrockmole.betterdining.models.UpcomingFavorite
import com.obrockmole.betterdining.network.DiningApi
import com.obrockmole.betterdining.network.RetrofitInstance
import com.obrockmole.betterdining.network.buildMultiItemQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UpcomingFavoritesRepository(private val favoriteItemDao: FavoriteItemDao) {
    private val diningApi: DiningApi = RetrofitInstance.api

    fun getUpcomingFavoritesFlow(): Flow<Result<List<UpcomingFavorite>>> {
        return favoriteItemDao.getAll().map { favorites ->
            try {
                val favoriteIds = favorites.map { it.itemId }
                if (favoriteIds.isEmpty()) {
                    return@map Result.success(emptyList())
                }

                val query = buildMultiItemQuery(favoriteIds)
                val request = GraphQLRequest(query = query, variables = emptyMap<String, Any>())
                val response = diningApi.getMultipleItems(request)

                if (response.isSuccessful && response.body() != null) {
                    val upcomingFavorites = response.body()!!.data.values.map { itemDetails ->
                        UpcomingFavorite(
                            itemId = itemDetails.itemId,
                            name = itemDetails.name,
                            appearances = itemDetails.appearances
                        )
                    }
                    Result.success(upcomingFavorites)
                } else {
                    Result.failure(Exception("Failed to fetch upcoming favorites"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
