package com.obrockmole.betterdining

import androidx.compose.ui.window.ComposeUIViewController
import com.obrockmole.betterdining.database.DataStoreFactory
import com.obrockmole.betterdining.database.DriverFactory

fun MainViewController() = ComposeUIViewController {
    App(driverFactory = DriverFactory(), dataStoreFactory = DataStoreFactory())
}