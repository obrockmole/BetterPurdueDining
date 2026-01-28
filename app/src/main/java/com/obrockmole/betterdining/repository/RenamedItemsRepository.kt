package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.RenamedItem
import com.obrockmole.betterdining.database.RenamedItemDao

class RenamedItemsRepository(private val renamedItemDao: RenamedItemDao) {
    suspend fun insert(renamedItem: RenamedItem) {
        renamedItemDao.insert(renamedItem)
    }

    suspend fun delete(renamedItem: RenamedItem) {
        renamedItemDao.delete(renamedItem)
    }

    suspend fun isRenamed(itemId: String): Boolean {
        return renamedItemDao.getRenamedItem(itemId) != null
    }

    suspend fun getRenamedItem(itemId: String): RenamedItem? {
        return renamedItemDao.getRenamedItem(itemId)
    }
}

