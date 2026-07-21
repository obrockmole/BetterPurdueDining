package com.obrockmole.kmpbetterdining.repository

import com.obrockmole.kmpbetterdining.database.RenamedDiningCourt
import com.obrockmole.kmpbetterdining.database.RenamedDiningCourtQueries
import com.obrockmole.kmpbetterdining.models.DiningCourtIdMap
import com.obrockmole.kmpbetterdining.utils.Logger

private const val LOG_TAG = "RenamedCourtsRepository"

class RenamedCourtsRepository(private val renamedCourtQueries: RenamedDiningCourtQueries) {
    suspend fun insert(renamedDiningCourt: RenamedDiningCourt) {
        renamedCourtQueries.insert(renamedDiningCourt.courtId, renamedDiningCourt.customName)
        Logger.LogDebug(LOG_TAG, "Set ${DiningCourtIdMap.diningCourtIdMap.entries.firstOrNull { it.value == renamedDiningCourt.courtId }?.key} (${renamedDiningCourt.courtId}) to ${renamedDiningCourt.customName}")
    }

    suspend fun delete(renamedDiningCourt: RenamedDiningCourt) {
        renamedCourtQueries.delete(renamedDiningCourt.courtId)
        Logger.LogDebug(LOG_TAG, "Reset ${DiningCourtIdMap.diningCourtIdMap.entries.firstOrNull { it.value == renamedDiningCourt.courtId }?.key} (${renamedDiningCourt.courtId})")
    }

    suspend fun isRenamed(courtId: String): Boolean {
        return renamedCourtQueries.isRenamed(courtId).executeAsOne()
    }

    suspend fun getRenamedCourt(courtId: String): RenamedDiningCourt? {
        return renamedCourtQueries.getRenamedCourt(courtId).executeAsOneOrNull()
    }
}

