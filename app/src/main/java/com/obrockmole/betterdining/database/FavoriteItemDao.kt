package com.obrockmole.betterdining.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteItem: FavoriteItem)

    @Delete
    suspend fun delete(favoriteItem: FavoriteItem)

    @Query("SELECT * FROM favorite_items")
    fun getAll(): Flow<List<FavoriteItem>>

    @Query("SELECT * FROM favorite_items WHERE itemId = :itemId")
    suspend fun getById(itemId: String): FavoriteItem?
}
