package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.FavoriteItem
import com.obrockmole.betterdining.database.FavoriteItemDao
import com.obrockmole.betterdining.viewmodel.FavoriteItemDisplay
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(private val favoriteItemDao: FavoriteItemDao) {
    fun getAll(): Flow<List<FavoriteItem>> = favoriteItemDao.getAll()

    fun getAllWithCustomNames(): Flow<List<FavoriteItemDisplay>> =
        favoriteItemDao.getAllWithCustomNames()

    suspend fun addFavorite(item: FavoriteItem) {
        favoriteItemDao.insert(item)
    }

    suspend fun removeFavorite(item: FavoriteItem) {
        favoriteItemDao.delete(item)
    }

    suspend fun isFavorite(itemId: String): Boolean {
        return favoriteItemDao.getById(itemId) != null
    }

    suspend fun getFavorite(itemId: String): FavoriteItem? {
        return favoriteItemDao.getById(itemId)
    }
}
