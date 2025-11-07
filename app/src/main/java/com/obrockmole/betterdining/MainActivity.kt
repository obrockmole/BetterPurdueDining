package com.obrockmole.betterdining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.obrockmole.betterdining.data.UserPreferencesRepository
import com.obrockmole.betterdining.ui.screens.DefaultScreenSelectionScreen
import com.obrockmole.betterdining.ui.screens.FavoritesScreen
import com.obrockmole.betterdining.ui.screens.HomeScreen
import com.obrockmole.betterdining.ui.screens.ItemDetailScreen
import com.obrockmole.betterdining.ui.screens.LicensesScreen
import com.obrockmole.betterdining.ui.screens.SettingsScreen
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetterPurdueDiningTheme {
                BetterPurdueDiningApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetterPurdueDiningApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }
    val defaultScreen by userPreferencesRepository.defaultScreen.collectAsState(initial = null)
    val homeViewModel: HomeViewModel = viewModel()

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isInitialScreenSet by rememberSaveable { mutableStateOf(false) }

    if (defaultScreen != null && !isInitialScreenSet) {
        currentDestination = when (defaultScreen) {
            "Favorites" -> AppDestinations.FAVORITES
            "Home" -> AppDestinations.HOME
            else -> AppDestinations.HOME
        }
        isInitialScreenSet = true
    }

    val navigatedFromFavorites by homeViewModel.selectedDiningCourt.collectAsState()
    LaunchedEffect(navigatedFromFavorites) {
        if (navigatedFromFavorites != null) {
            currentDestination = AppDestinations.HOME
        }
    }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            BackHandler(enabled = currentDestination != AppDestinations.HOME) {
                currentDestination = AppDestinations.HOME
            }

            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    AppDestinations.entries.forEach {
                        item(
                            icon = {
                                Icon(
                                    it.icon,
                                    contentDescription = it.label
                                )
                            },
                            label = { Text(it.label) },
                            selected = it == currentDestination,
                            onClick = { currentDestination = it }
                        )
                    }
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (defaultScreen == null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    } else {
                        when (currentDestination) {
                            AppDestinations.HOME -> {
                                HomeScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onNavigateToItem = { itemId ->
                                        navController.navigate("item/$itemId")
                                    },
                                    viewModel = homeViewModel
                                )
                            }
                            AppDestinations.FAVORITES -> {
                                FavoritesScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onNavigateToItem = { itemId ->
                                        navController.navigate("item/$itemId")
                                    },
                                    homeViewModel = homeViewModel
                                )
                            }
                            AppDestinations.SETTINGS -> {
                                SettingsScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onNavigateToDefaultScreen = {
                                        navController.navigate("settings/defaultScreen")
                                    },
                                    onNavigateToLicensesScreen = {
                                        navController.navigate("settings/licenses")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        composable("item/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            if (itemId != null) {
                ItemDetailScreen(
                    itemId = itemId
                )
            }
        }
        composable("settings/defaultScreen") {
            DefaultScreenSelectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings/licenses") {
            LicensesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    SETTINGS("Settings", Icons.Default.Settings)
}


@Preview(showBackground = true)
@Composable
fun BetterPurdueDiningPreview() {
    BetterPurdueDiningTheme {
        BetterPurdueDiningApp()
    }
}
