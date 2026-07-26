package com.obrockmole.kmpbetterdining.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

actual class DataStoreFactory(private val context: Context) {
    actual fun createDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = { context.filesDir.resolve("settings.preferences_pb").absolutePath.toPath() }
        )
    }
}