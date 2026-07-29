package com.obrockmole.kmpbetterdining.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val homeDir = System.getProperty("user.home")

        val appDir = File(homeDir, ".betterdining")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val databaseFile = File(appDir, "betterdining.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        if (!databaseFile.exists() || databaseFile.length() == 0L) {
            BetterDiningDatabase.Schema.create(driver)
        }

        return driver
    }
}