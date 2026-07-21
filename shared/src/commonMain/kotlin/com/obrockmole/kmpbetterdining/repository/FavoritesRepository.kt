package com.obrockmole.kmpbetterdining.repository

import com.obrockmole.kmpbetterdining.database.FavoriteItem
import com.obrockmole.kmpbetterdining.database.FavoriteItemQueries
import com.obrockmole.kmpbetterdining.utils.Logger

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

    fun getAll(): List<FavoriteItem> {
        return favoriteItemQueries.getAll().executeAsList()
    }
}
