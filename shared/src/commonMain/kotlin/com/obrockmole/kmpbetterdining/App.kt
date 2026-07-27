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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.obrockmole.kmpbetterdining.database.BetterDiningDatabase
import com.obrockmole.kmpbetterdining.database.DataStoreFactory
import com.obrockmole.kmpbetterdining.database.DriverFactory
import com.obrockmole.kmpbetterdining.repository.*
import com.obrockmole.kmpbetterdining.ui.screens.*
import com.obrockmole.kmpbetterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.kmpbetterdining.utils.BackHandler
import com.obrockmole.kmpbetterdining.utils.Logger
import com.obrockmole.kmpbetterdining.viewmodel.*
import kmpbetterdining.shared.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "MainActivity"

@Composable
fun App(driverFactory: DriverFactory, dataStoreFactory: DataStoreFactory) {
    val database = remember {
        BetterDiningDatabase(driverFactory.createDriver())
    }

    val dataStore = remember {
        dataStoreFactory.createDataStore()
    }

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            StartLocationsRepository(),
            RenamedCourtsRepository(database.renamedDiningCourtQueries)
        )
    )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            SettingsRepository(),
            UserPreferencesRepository(dataStore)
        )
    )

    val appTheme by settingsViewModel.appTheme.collectAsState(initial = "Material")
    val logAmount by settingsViewModel.logAmount.collectAsState(initial = "Minimal")
    Logger.setLogAmount(logAmount)

    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isInitialScreenSet by rememberSaveable { mutableStateOf(false) }

    BetterPurdueDiningTheme(theme = appTheme, key = currentDestination) {
        val defaultScreen by settingsViewModel.defaultScreen.collectAsState(initial = null)
        Logger.LogDebug(LOG_TAG, "Default screen: $defaultScreen")
        val navStyle by settingsViewModel.navStyle.collectAsState(initial = null)
        Logger.LogDebug(LOG_TAG, "Nav style: $navStyle")

        if (defaultScreen != null && !isInitialScreenSet) {
            currentDestination = when (defaultScreen) {
                "Favorites" -> AppDestinations.FAVORITES
                "Home" -> AppDestinations.HOME
                else -> AppDestinations.HOME
            }
            Logger.LogDebug(LOG_TAG, "Set initial screen to: $currentDestination")
            isInitialScreenSet = true
        }

        val navigatedFromFavorites by homeViewModel.selectedDiningCourt.collectAsState()
        LaunchedEffect(navigatedFromFavorites) {
            Logger.LogInfo(LOG_TAG, "Navigated from favorites")
            currentDestination = AppDestinations.HOME
        }

        NavHost(navController = navController, startDestination = MainRoute) {
            composable<MainRoute> {
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
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToFoodLocation = { locationName, locationId ->
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to location $locationName ($locationId)")

                                                    navController.navigate(
                                                        LocationRoute(
                                                            locationId = locationId,
                                                            locationName = locationName,
                                                            initialMealName = homeViewModel.selectedMealName.value,
                                                            initialDate = homeViewModel.selectedDate.value,
                                                            initialItemName = homeViewModel.selectedItem.value
                                                        )
                                                    )
                                                },
                                                viewModel = homeViewModel,
                                                searchViewModel = viewModel(
                                                    factory = SearchViewModelFactory(
                                                        SearchRepository(),
                                                        RenamedItemsRepository(database.renamedItemQueries)
                                                    )
                                                )
                                            )
                                        }

                                        AppDestinations.FAVORITES -> {
                                            FavoritesScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToItem = { itemName, itemId ->
                                                    navController.navigate(ItemRoute(itemId = itemId, itemName = itemName))
                                                },
                                                favoritesViewModel = viewModel(
                                                    factory = FavoritesViewModelFactory(
                                                        FavoritesRepository(database.favoriteItemQueries)
                                                    )
                                                ),
                                                homeViewModel = homeViewModel,
                                                showHeader = true
                                            )
                                        }

                                        AppDestinations.SETTINGS -> {
                                            SettingsScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToDefaultScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to default screen settings")
                                                    navController.navigate(DefaultScreenRoute)
                                                },
                                                onNavigateToTheme = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to theme settings")
                                                    navController.navigate(ThemeRoute)
                                                },
                                                onNavigateToNavStyle = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to nav style settings")
                                                    navController.navigate(NavStyleRoute)
                                                },
                                                onNavigateToLicensesScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to licenses")
                                                    navController.navigate(LicensesRoute)
                                                },
                                                onNavigateToLogAmount = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to log amount settings")
                                                    navController.navigate(LogAmountRoute)
                                                },
                                                settingsViewModel = settingsViewModel
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
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToFoodLocation = { locationName, locationId ->
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to location $locationName ($locationId)")

                                                    navController.navigate(
                                                        LocationRoute(
                                                            locationId = locationId,
                                                            locationName = locationName,
                                                            initialMealName = homeViewModel.selectedMealName.value,
                                                            initialDate = homeViewModel.selectedDate.value,
                                                            initialItemName = homeViewModel.selectedItem.value
                                                        )
                                                    )
                                                },
                                                viewModel = homeViewModel,
                                                searchViewModel = viewModel(
                                                    factory = SearchViewModelFactory(
                                                        SearchRepository(),
                                                        RenamedItemsRepository(database.renamedItemQueries)
                                                    )
                                                )
                                            )
                                        }

                                        AppDestinations.FAVORITES -> {
                                            FavoritesScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToItem = { itemName, itemId ->
                                                    navController.navigate(ItemRoute(itemId = itemId, itemName = itemName))
                                                },
                                                favoritesViewModel = viewModel(
                                                    factory = FavoritesViewModelFactory(
                                                        FavoritesRepository(database.favoriteItemQueries)
                                                    )
                                                ),
                                                homeViewModel = homeViewModel,
                                                showHeader = false
                                            )
                                        }

                                        AppDestinations.SETTINGS -> {
                                            SettingsScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToDefaultScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to default screen settings")
                                                    navController.navigate(DefaultScreenRoute)
                                                },
                                                onNavigateToTheme = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to theme settings")
                                                    navController.navigate(ThemeRoute)
                                                },
                                                onNavigateToNavStyle = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to nav style settings")
                                                    navController.navigate(NavStyleRoute)
                                                },
                                                onNavigateToLicensesScreen = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main drawer: Navigating to licenses")
                                                    navController.navigate(LicensesRoute)
                                                },
                                                onNavigateToLogAmount = {
                                                    Logger.LogInfo(LOG_TAG, "NavHost main suit: Navigating to log amount settings")
                                                    navController.navigate(LogAmountRoute)
                                                },
                                                settingsViewModel = settingsViewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            composable<LocationRoute>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) { backStackEntry ->
                val routeData = backStackEntry.toRoute<LocationRoute>()
                Logger.LogDebug(LOG_TAG, "NavHost location: Entered location composable with id $routeData.locationId")

                val menuViewModel: MenuViewModel = viewModel(
                    key = routeData.locationId,
                    factory = MenuViewModelFactory(
                        MenuRepository(),
                        RenamedItemsRepository(database.renamedItemQueries),
                        RenamedCourtsRepository(database.renamedDiningCourtQueries)
                    )
                )

                FoodLocationDetailScreen(
                    name = routeData.locationName,
                    courtId = routeData.locationId,
                    onNavigateBack = { navController.popBackStack() },
                    menuViewModel = menuViewModel,
                    onNavigateToItem = { itemName, itemId ->
                        navController.navigate(ItemRoute(itemId = itemId, itemName = itemName))
                    },
                    initialMealName = routeData.initialMealName,
                    initialDate = routeData.initialDate,
                    initialItemName = routeData.initialItemName
                )
            }

            composable<ItemRoute>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) { backStackEntry ->
                val routeData = backStackEntry.toRoute<ItemRoute>()
                Logger.LogDebug(LOG_TAG, "NavHost item: Entered item composable with id ${routeData.itemId}")

                val itemViewModel: ItemViewModel = viewModel(
                    factory = ItemViewModelFactory(
                        MenuRepository(),
                        FavoritesRepository(database.favoriteItemQueries),
                        RenamedItemsRepository(database.renamedItemQueries)
                    )
                )

                ItemDetailScreen(
                    itemName = routeData.itemName,
                    itemId = routeData.itemId,
                    onNavigateBack = { navController.popBackStack() },
                    homeViewModel = homeViewModel,
                    itemViewModel = itemViewModel
                )
            }

            composable<DefaultScreenRoute>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                DefaultScreenSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from DefaultScreenSelectionScreen")
                        navController.popBackStack()
                    },
                    settingsViewModel = settingsViewModel
                )
            }

            composable<ThemeRoute>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from ThemeSelectionScreen")
                        navController.popBackStack()
                    },
                    settingsViewModel = settingsViewModel
                )
            }

            composable<NavStyleRoute>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                NavStyleSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from NavStyleSelectionScreen")
                        navController.popBackStack()
                    },
                    settingsViewModel = settingsViewModel
                )
            }

            composable<LogAmountRoute>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                LogAmountSelectionScreen(
                    onNavigateBack = {
                        Logger.LogDebug(LOG_TAG, "NavHost settings: Navigating back from LogAmountSelectionScreen")
                        navController.popBackStack()
                    },
                    settingsViewModel = settingsViewModel
                )
            }

            composable<LicensesRoute>(
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