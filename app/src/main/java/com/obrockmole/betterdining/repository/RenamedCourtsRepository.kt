package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.RenamedDiningCourt
import com.obrockmole.betterdining.database.RenamedDiningCourtDao
import com.obrockmole.betterdining.models.DiningCourtIdMap
import com.obrockmole.betterdining.utils.Logger

private const val LOG_TAG = "RenamedCourtsRepository"

class RenamedCourtsRepository(private val renamedDiningCourtDao: RenamedDiningCourtDao) {
    suspend fun insert(renamedDiningCourt: RenamedDiningCourt) {
        renamedDiningCourtDao.insert(renamedDiningCourt)
        Logger.LogDebug(LOG_TAG, "Set ${DiningCourtIdMap.diningCourtIdMap.entries.firstOrNull { it.value == renamedDiningCourt.courtId }?.key} (${renamedDiningCourt.courtId}) to ${renamedDiningCourt.customName}")
    }

    suspend fun delete(renamedDiningCourt: RenamedDiningCourt) {
        renamedDiningCourtDao.delete(renamedDiningCourt)
        Logger.LogDebug(LOG_TAG, "Reset ${DiningCourtIdMap.diningCourtIdMap.entries.firstOrNull { it.value == renamedDiningCourt.courtId }?.key} (${renamedDiningCourt.courtId})")
    }

    suspend fun isRenamed(courtId: String): Boolean {
        return renamedDiningCourtDao.getRenamedCourt(courtId) != null
    }

    suspend fun getRenamedCourt(courtId: String): RenamedDiningCourt? {
        return renamedDiningCourtDao.getRenamedCourt(courtId)
    }
}

