package com.obrockmole.kmpbetterdining

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.obrockmole.kmpbetterdining.database.DataStoreFactory
import com.obrockmole.kmpbetterdining.database.DriverFactory

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP Better Dining",
    ) {
        App(driverFactory = DriverFactory(), dataStoreFactory = DataStoreFactory())
    }
}