package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.FavoriteItem
import com.obrockmole.betterdining.database.FavoriteItemDao
import com.obrockmole.betterdining.utils.Logger
import com.obrockmole.betterdining.viewmodel.FavoriteItemDisplay
import kotlinx.coroutines.flow.Flow

private const val LOG_TAG = "FavoritesRepository"

class FavoritesRepository(private val favoriteItemDao: FavoriteItemDao) {
    fun getAll(): Flow<List<FavoriteItem>> = favoriteItemDao.getAll()

    fun getAllWithCustomNames(): Flow<List<FavoriteItemDisplay>> =
        favoriteItemDao.getAllWithCustomNames()

    suspend fun addFavorite(item: FavoriteItem) {
        favoriteItemDao.insert(item)
        Logger.LogDebug(LOG_TAG, "Added ${item.name} (${item.itemId}) as a favorite")
    }

    suspend fun removeFavorite(item: FavoriteItem) {
        favoriteItemDao.delete(item)
        Logger.LogDebug(LOG_TAG, "Removed ${item.name} (${item.itemId}) from favorites")
    }

    suspend fun isFavorite(itemId: String): Boolean {
        return favoriteItemDao.getById(itemId) != null
    }

    suspend fun getFavorite(itemId: String): FavoriteItem? {
        return favoriteItemDao.getById(itemId)
    }
}
