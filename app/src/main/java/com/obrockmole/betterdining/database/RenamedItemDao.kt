package com.obrockmole.betterdining.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RenamedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(renamedItem: RenamedItem)

    @Delete
    suspend fun delete(renamedItem: RenamedItem)

    @Query("SELECT * FROM renamed_items WHERE itemId = :itemId")
    suspend fun getRenamedItem(itemId: String): RenamedItem?
}

