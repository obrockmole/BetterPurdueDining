package com.obrockmole.kmpbetterdining.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect class DataStoreFactory {
    fun createDataStore(): DataStore<Preferences>
}