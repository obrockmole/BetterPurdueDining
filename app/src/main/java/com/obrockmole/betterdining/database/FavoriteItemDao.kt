package com.obrockmole.betterdining.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obrockmole.betterdining.viewmodel.FavoriteItemDisplay
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteItem: FavoriteItem)

    @Delete
    suspend fun delete(favoriteItem: FavoriteItem)

    @Query("SELECT * FROM favorite_items")
    fun getAll(): Flow<List<FavoriteItem>>

    @Query("SELECT f.itemId, COALESCE(r.customName, f.name) as name, f.dateAdded FROM favorite_items f LEFT JOIN renamed_items r ON f.itemId = r.itemId")
    fun getAllWithCustomNames(): Flow<List<FavoriteItemDisplay>>

    @Query("SELECT * FROM favorite_items WHERE itemId = :itemId")
    suspend fun getById(itemId: String): FavoriteItem?
}
