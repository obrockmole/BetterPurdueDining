package com.obrockmole.kmpbetterdining.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:betterdining.db")
        BetterDiningDatabase.Schema.create(driver)
        return driver
    }
}