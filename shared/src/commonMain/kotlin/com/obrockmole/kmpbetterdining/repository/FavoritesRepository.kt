package com.obrockmole.kmpbetterdining.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.obrockmole.kmpbetterdining.database.FavoriteItem
import com.obrockmole.kmpbetterdining.database.FavoriteItemQueries
import com.obrockmole.kmpbetterdining.database.GetAllWithCustomNames
import com.obrockmole.kmpbetterdining.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

private const val LOG_TAG = "FavoritesRepository"

class FavoritesRepository(private val favoriteItemQueries: FavoriteItemQueries) {
    fun addFavorite(favoriteItem: FavoriteItem) {
        favoriteItemQueries.addFavorite(favoriteItem.itemId, favoriteItem.name, favoriteItem.dateAdded)
        Logger.LogDebug(LOG_TAG, "Added ${favoriteItem.name} (${favoriteItem.itemId}) as a favorite")
    }

    fun removeFavorite(favoriteItem: FavoriteItem) {
        favoriteItemQueries.removeFavorite(favoriteItem.itemId)
        Logger.LogDebug(LOG_TAG, "Removed ${favoriteItem.name} (${favoriteItem.itemId}) from favorites")
    }

    fun getFavorite(itemId: String): FavoriteItem? {
        return favoriteItemQueries.getFavorite(itemId).executeAsOneOrNull()
    }

    fun isFavorite(itemId: String): Boolean {
        return favoriteItemQueries.isFavorite(itemId).executeAsOne()
    }

    fun getAll(): Flow<List<FavoriteItem>> {
        return favoriteItemQueries.getAll().asFlow().mapToList(Dispatchers.IO)
    }

    fun getAllWithCustomNames(): Flow<List<GetAllWithCustomNames>> {
        return favoriteItemQueries.getAllWithCustomNames().asFlow().mapToList(Dispatchers.IO)
    }
}
