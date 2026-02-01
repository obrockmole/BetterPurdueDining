package com.obrockmole.betterdining.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "renamed_dining_courts")
data class RenamedDiningCourt(
    @PrimaryKey val courtId: String,
    val customName: String
)