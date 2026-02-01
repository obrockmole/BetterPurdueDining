package com.obrockmole.betterdining.repository

import com.obrockmole.betterdining.database.RenamedDiningCourt
import com.obrockmole.betterdining.database.RenamedDiningCourtDao

class RenamedCourtsRepository(private val renamedDiningCourtDao: RenamedDiningCourtDao) {
    suspend fun insert(renamedDiningCourt: RenamedDiningCourt) {
        renamedDiningCourtDao.insert(renamedDiningCourt)
    }

    suspend fun delete(renamedDiningCourt: RenamedDiningCourt) {
        renamedDiningCourtDao.delete(renamedDiningCourt)
    }

    suspend fun isRenamed(courtId: String): Boolean {
        return renamedDiningCourtDao.getRenamedCourt(courtId) != null
    }

    suspend fun getRenamedCourt(courtId: String): RenamedDiningCourt? {
        return renamedDiningCourtDao.getRenamedCourt(courtId)
    }
}

