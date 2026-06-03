package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.RenamedItem
import com.obrockmole.betterdining.database.RenamedItemDao
import com.obrockmole.betterdining.utils.Logger

private const val LOG_TAG = "RenamedItemsRepository"

class RenamedItemsRepository(private val renamedItemDao: RenamedItemDao) {
    suspend fun insert(renamedItem: RenamedItem) {
        renamedItemDao.insert(renamedItem)
        Logger.LogDebug(LOG_TAG, "Set the custom name of item '${renamedItem.itemId}' to ${renamedItem.customName}")
    }

    suspend fun delete(renamedItem: RenamedItem) {
        renamedItemDao.delete(renamedItem)
        Logger.LogDebug(LOG_TAG, "Reset the custom name of item '${renamedItem.itemId}'")
    }

    suspend fun isRenamed(itemId: String): Boolean {
        return renamedItemDao.getRenamedItem(itemId) != null
    }

    suspend fun getRenamedItem(itemId: String): RenamedItem? {
        return renamedItemDao.getRenamedItem(itemId)
    }
}

