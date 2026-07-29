package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.RenamedItem
import com.obrockmole.betterdining.database.RenamedItemQueries
import com.obrockmole.betterdining.utils.Logger

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

