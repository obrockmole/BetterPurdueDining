package com.obrockmole.betterdining

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.obrockmole.betterdining.database.DataStoreFactory
import com.obrockmole.betterdining.database.DriverFactory

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Better Purdue Dining",
    ) {
        App(driverFactory = DriverFactory(), dataStoreFactory = DataStoreFactory())
    }
}