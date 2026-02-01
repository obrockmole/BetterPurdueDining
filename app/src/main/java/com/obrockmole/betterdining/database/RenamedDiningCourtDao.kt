package com.obrockmole.betterdining.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RenamedDiningCourtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(renamedDiningCourt: RenamedDiningCourt)

    @Delete
    suspend fun delete(renamedDiningCourt: RenamedDiningCourt)

    @Query("SELECT * FROM renamed_dining_courts WHERE courtId = :courtId")
    suspend fun getRenamedCourt(courtId: String): RenamedDiningCourt?
}

