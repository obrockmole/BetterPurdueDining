package com.obrockmole.kmpbetterdining

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.obrockmole.kmpbetterdining.ui.screens.*
import com.obrockmole.kmpbetterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.kmpbetterdining.utils.BackHandler
import com.obrockmole.kmpbetterdining.utils.Logger
import kmpbetterdining.shared.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "MainActivity"

@Composable
fun App() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }


    val defaultScreen by remember { mutableStateOf("Home") }
    val navStyle by remember { mutableStateOf("Bottom") }

    BetterPurdueDiningTheme {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                BackHandler(enabled = currentDestination != AppDestinations.HOME) {
                    Logger.LogInfo(LOG_TAG, "NavHost main: Navigating to HOME")
                    currentDestination = AppDestinations.HOME
                }

                if (defaultScreen == null || navStyle == null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    when (navStyle) {
                        "Bottom" -> {
                            NavigationSuiteScaffold(
                                navigationSuiteItems = {
                                    AppDestinations.entries.forEach {
                                        item(
                                            icon = {
                                                Icon(
                                                    painter = painterResource(it.resource),
                                                    contentDescription = it.label
                                                )
                                            },
                                            label = { Text(it.label) },
                                            selected = it == currentDestination,
                                            onClick = {
                                                Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to ${it.label}")
                                                currentDestination = it
                                            }
                                        )
                                    }
                                }
                            ) {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize()
                                ) { innerPadding ->
                                    when (currentDestination) {
                                        AppDestinations.HOME -> {
                                            HomeScreen(
                                                modifier = Modifier.padding(innerPadding)
                                            )
                                        }

                                        AppDestinations.FAVORITES -> {
                                            FavoritesScreen(
                                                modifier = Modifier.padding(innerPadding)
                                            )
                                        }

                                        AppDestinations.SETTINGS -> {
                                            SettingsScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToDefaultScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to default screen settings")
                                                    navController.navigate("settings/defaultScreen")
                                                },
                                                onNavigateToTheme = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to theme settings")
                                                    navController.navigate("settings/theme")
                                                },
                                                onNavigateToNavStyle = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to nav style settings")
                                                    navController.navigate("settings/navStyle")
                                                },
                                                onNavigateToLicensesScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to licenses")
                                                    navController.navigate("settings/licenses")
                                                },
                                                onNavigateToLogLevel = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to log level settings")
                                                    navController.navigate("settings/logLevel")
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        "Side" -> {
                            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                            val scope = rememberCoroutineScope()

                            ModalNavigationDrawer(
                                drawerState = drawerState,
                                drawerContent = {
                                    ModalDrawerSheet {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(Res.drawable.app_icon),
                                                    modifier = Modifier.size(48.dp),
                                                    contentDescription = "App Icon"
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = "Better Purdue Dining",
                                                    style = MaterialTheme.typography.headlineMedium
                                                )
                                            }
                                        }

                                        AppDestinations.entries.forEach { destination ->
                                            NavigationDrawerItem(
                                                icon = {
                                                    Icon(
                                                        painter = painterResource(destination.resource),
                                                        contentDescription = destination.label
                                                    )
                                                },
                                                label = { Text(destination.label) },
                                                selected = destination == currentDestination,
                                                onClick = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to ${destination.label}")
                                                    currentDestination = destination
                                                    scope.launch {
                                                        drawerState.close()
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            ) {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    topBar = {
                                        TopAppBar(
                                            title = { Text(currentDestination.label) },
                                            navigationIcon = {
                                                IconButton(onClick = {
                                                    scope.launch {
                                                        Logger.LogDebug(LOG_TAG, "NavHost main drawer: Opening")
                                                        drawerState.open()
                                                    }
                                                }) {
                                                    Icon(
                                                        painter = painterResource(Res.drawable.menu),
                                                        contentDescription = "Menu"
                                                    )
                                                }
                                            }
                                        )
                                    }
                                ) { innerPadding ->
                                    when (currentDestination) {
                                        AppDestinations.HOME -> {
                                            HomeScreen(
                                                modifier = Modifier.padding(innerPadding)
                                            )
                                        }

                                        AppDestinations.FAVORITES -> {
                                            FavoritesScreen(
                                                modifier = Modifier.padding(innerPadding)
                                            )
                                        }

                                        AppDestinations.SETTINGS -> {
                                            SettingsScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToDefaultScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to default screen settings")
                                                    navController.navigate("settings/defaultScreen")
                                                },
                                                onNavigateToTheme = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to theme settings")
                                                    navController.navigate("settings/theme")
                                                },
                                                onNavigateToNavStyle = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to nav style settings")
                                                    navController.navigate("settings/navStyle")
                                                },
                                                onNavigateToLicensesScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to licenses")
                                                    navController.navigate("settings/licenses")
                                                },
                                                onNavigateToLogLevel = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to log level settings")
                                                    navController.navigate("settings/logLevel")
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            composable(
                "settings/defaultScreen",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                DefaultScreenSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from DefaultScreenSelectionScreen")
                        navController.popBackStack()
                    }
                )
            }

            composable(
                "settings/theme",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from ThemeSelectionScreen")
                        navController.popBackStack()
                    }
                )
            }

            composable(
                "settings/navStyle",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                NavStyleSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from NavStyleSelectionScreen")
                        navController.popBackStack()
                    }
                )
            }

            composable(
                "settings/licenses",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                LicensesScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from LicensesScreen")
                        navController.popBackStack()
                    }
                )
            }

            composable(
                "settings/logLevel",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                LogLevelSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from LogLevelSelectionScreen")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val resource: DrawableResource,
) {
    HOME("Home", Res.drawable.home),
    FAVORITES("Favorites", Res.drawable.favorite),
    SETTINGS("Settings", Res.drawable.settings)
}