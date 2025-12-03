package com.obrockmole.betterdining.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items")
data class FavoriteItem(
    @PrimaryKey val itemId: String,
    val name: String,
    val dateAdded: String? = null
)
