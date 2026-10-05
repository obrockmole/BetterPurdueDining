package com.obrockmole.betterdining.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

actual class DataStoreFactory {
    actual fun createDataStore(): DataStore<Preferences> {
        val homeDir = System.getProperty("user.home")

        val appDir = File(homeDir, ".betterdining")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val datastoreFile = File(appDir, "settings.preferences_pb")

        return PreferenceDataStoreFactory.createWithPath(
            produceFile = { datastoreFile.absolutePath.toPath() }
        )
    }
}