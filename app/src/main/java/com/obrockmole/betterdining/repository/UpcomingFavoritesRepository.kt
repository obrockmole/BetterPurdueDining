package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.FavoriteItemDao
import com.obrockmole.betterdining.models.GraphQLRequest
import com.obrockmole.betterdining.models.UpcomingFavorite
import com.obrockmole.betterdining.network.DiningApi
import com.obrockmole.betterdining.network.RetrofitInstance
import com.obrockmole.betterdining.network.buildMultiItemQuery
import kotlinx.coroutines.flow.first

class UpcomingFavoritesRepository(private val favoriteItemDao: FavoriteItemDao) {
    private val diningApi: DiningApi = RetrofitInstance.api

    suspend fun getUpcomingFavorites(): Result<List<UpcomingFavorite>> {
        return try {
            val favoriteIds = favoriteItemDao.getAll().first().map { it.itemId }
            if (favoriteIds.isEmpty()) {
                return Result.success(emptyList())
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
