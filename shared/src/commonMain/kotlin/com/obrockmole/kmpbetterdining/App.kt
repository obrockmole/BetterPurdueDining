package com.obrockmole.kmpbetterdining

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kmpbetterdining.shared.generated.resources.Res
import kmpbetterdining.shared.generated.resources.favorite
import kmpbetterdining.shared.generated.resources.home
import kmpbetterdining.shared.generated.resources.settings
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("Home") }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == "Home",
                        onClick = { currentScreen = "Home" },
                        icon = { Icon(painterResource(Res.drawable.home), contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "Favorites",
                        onClick = { currentScreen = "Favorites" },
                        icon = { Icon(painterResource(Res.drawable.favorite), contentDescription = "Favorites") },
                        label = { Text("Favorites") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "Settings",
                        onClick = { currentScreen = "Settings" },
                        icon = { Icon(painterResource(Res.drawable.settings), contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                "Home" -> HomeScreen(
                    modifier = modifier
                )
                "Favorites" -> FavoritesScreen(
                    modifier = modifier
                )
                "Settings" -> SettingsScreen(
                    modifier = modifier
                )
            }
        }
    }
}