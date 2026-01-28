package com.obrockmole.betterdining.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "renamed_items")
data class RenamedItem(
    @PrimaryKey val itemId: String,
    val customName: String
)