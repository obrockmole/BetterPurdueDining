package com.obrockmole.kmpbetterdining.repository

import com.obrockmole.kmpbetterdining.database.RenamedItem
import com.obrockmole.kmpbetterdining.database.RenamedItemQueries
import com.obrockmole.kmpbetterdining.utils.Logger

private const val LOG_TAG = "RenamedItemsRepository"

class RenamedItemsRepository(private val renamedItemQueries: RenamedItemQueries) {
    fun insert(renamedItem: RenamedItem) {
        renamedItemQueries.insert(renamedItem.itemId, renamedItem.customName)
        Logger.LogDebug(LOG_TAG, "Set the custom name of item '${renamedItem.itemId}' to ${renamedItem.customName}")
    }

    fun delete(renamedItem: RenamedItem) {
        renamedItemQueries.delete(renamedItem.itemId)
        Logger.LogDebug(LOG_TAG, "Reset the custom name of item '${renamedItem.itemId}'")
    }

    fun isRenamed(itemId: String): Boolean {
        return renamedItemQueries.isRenamed(itemId).executeAsOne()
    }

    fun getRenamedItem(itemId: String): RenamedItem? {
        return renamedItemQueries.getRenamedItem(itemId).executeAsOneOrNull()
    }
}

