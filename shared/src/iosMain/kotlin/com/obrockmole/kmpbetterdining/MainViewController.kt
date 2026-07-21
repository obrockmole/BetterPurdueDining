package com.obrockmole.kmpbetterdining

import androidx.compose.ui.window.ComposeUIViewController
import com.obrockmole.kmpbetterdining.database.DriverFactory

fun MainViewController() = ComposeUIViewController {
    App(driverFactory = DriverFactory())
}