package com.obrockmole.betterdining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.obrockmole.betterdining.database.DataStoreFactory
import com.obrockmole.betterdining.database.DriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                driverFactory = DriverFactory(applicationContext),
                dataStoreFactory = DataStoreFactory(applicationContext)
            )
        }
    }
}