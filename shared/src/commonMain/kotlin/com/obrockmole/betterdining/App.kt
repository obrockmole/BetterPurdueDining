package com.obrockmole.betterdining

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.obrockmole.betterdining.database.BetterDiningDatabase
import com.obrockmole.betterdining.database.DataStoreFactory
import com.obrockmole.betterdining.database.DriverFactory
import com.obrockmole.betterdining.repository.*
import com.obrockmole.betterdining.ui.screens.*
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.utils.Logger
import com.obrockmole.betterdining.viewmodel.*
import betterpurduedining.shared.generated.resources.*
import kotlinx.coroutines.launch
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

    val defaultScreen by settingsViewModel.defaultScreen.collectAsState(initial = null)
    val navStyle by settingsViewModel.navStyle.collectAsState(initial = null)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = navBackStackEntry?.destination?.route

    BetterPurdueDiningTheme(theme = appTheme, key = currentRoute to currentDestination) {
        Logger.LogDebug(LOG_TAG, "Default screen: $defaultScreen")
        Logger.LogDebug(LOG_TAG, "Nav style: $navStyle")

        if (defaultScreen == null || navStyle == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            return@BetterPurdueDiningTheme
        }

        val atRoot = currentDestination?.hierarchy?.any {
            it.hasRoute<HomeRoute>() || it.hasRoute<FavoritesRoute>() || it.hasRoute<SettingsRoute>()
        } == true

        val startDestination = if (defaultScreen?.equals("Favorites") == true) FavoritesRoute else HomeRoute
        val navHost = @Composable { paddingValues: PaddingValues, onOpenDrawer: (() -> Unit)? ->
            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = startDestination
            ) {
                composable<HomeRoute>(
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    Logger.LogInfo(LOG_TAG, "NavHost main: Navigating to HOME")
                    HomeScreen(
                        onNavigateToFoodLocation = { name, id ->
                            navController.navigate(
                                LocationRoute(
                                    id,
                                    name,
                                    homeViewModel.selectedMealName.value,
                                    homeViewModel.selectedDate.value,
                                    homeViewModel.selectedItem.value
                                )
                            )
                        },
                        onNavigateToSearch = {
                            navController.navigate(SearchRoute)
                        },
                        onOpenDrawer = onOpenDrawer,
                        viewModel = homeViewModel
                    )
                }

                composable<SearchRoute>(
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    Logger.LogInfo(LOG_TAG, "NavHost main: Navigating to SEARCH")
                    SearchScreen(
                        onNavigateBack = { navController.popBackStack() },
                        homeViewModel = homeViewModel
                    )
                }

                composable<FavoritesRoute>(
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    Logger.LogInfo(LOG_TAG, "NavHost main: Navigating to FAVORITES")
                    FavoritesScreen(
                        onNavigateToItem = { name, id -> navController.navigate(ItemRoute(id, name)) },
                        onOpenDrawer = onOpenDrawer,
                        favoritesViewModel = viewModel(factory = FavoritesViewModelFactory(FavoritesRepository(database.favoriteItemQueries))),
                        homeViewModel = homeViewModel
                    )
                }

                composable<SettingsRoute>(
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    Logger.LogInfo(LOG_TAG, "NavHost main: Navigating to SETTINGS")
                    SettingsScreen(
                        onNavigateToDefaultScreen = { navController.navigate(DefaultScreenRoute) },
                        onNavigateToTheme = { navController.navigate(ThemeRoute) },
                        onNavigateToNavStyle = { navController.navigate(NavStyleRoute) },
                        onNavigateToLicensesScreen = { navController.navigate(LicensesRoute) },
                        onNavigateToLogAmount = { navController.navigate(LogAmountRoute) },
                        onOpenDrawer = onOpenDrawer,
                        settingsViewModel = settingsViewModel
                    )
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
                            navController.navigate(ItemRoute(itemId, itemName))
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
                        onNavigateBack = {
                            navController.navigate(HomeRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            }
                        },
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

        if (atRoot && navStyle == "Bottom") {
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    item(
                        icon = { Icon(painterResource(Res.drawable.home), "Home") },
                        label = { Text("Home") },
                        selected = currentDestination.hasRoute<HomeRoute>(),
                        onClick = {
                            navController.navigate(HomeRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    item(
                        icon = { Icon(painterResource(Res.drawable.favorite), "Favorites") },
                        label = { Text("Favorites") },
                        selected = currentDestination.hasRoute<FavoritesRoute>(),
                        onClick = {
                            navController.navigate(FavoritesRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    item(
                        icon = { Icon(painterResource(Res.drawable.settings), "Settings") },
                        label = { Text("Settings") },
                        selected = currentDestination.hasRoute<SettingsRoute>(),
                        onClick = {
                            navController.navigate(SettingsRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            ) {
                navHost(PaddingValues(0.dp), null)
            }
        } else if (atRoot && navStyle == "Side") {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
                                Text(text = "Better Purdue Dining", style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                        NavigationDrawerItem(
                            icon = { Icon(painterResource(Res.drawable.home), "Home") },
                            label = { Text("Home") },
                            selected = currentDestination.hasRoute<HomeRoute>(),
                            onClick = {
                                navController.navigate(HomeRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(painterResource(Res.drawable.favorite), "Favorites") },
                            label = { Text("Favorites") },
                            selected = currentDestination.hasRoute<FavoritesRoute>(),
                            onClick = {
                                navController.navigate(FavoritesRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch { drawerState.close() }
                            }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(painterResource(Res.drawable.settings), "Settings") },
                            label = { Text("Settings") },
                            selected = currentDestination.hasRoute<SettingsRoute>(),
                            onClick = {
                                navController.navigate(SettingsRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            ) {
                Scaffold {
                    navHost(PaddingValues(0.dp)) { scope.launch { drawerState.open() } }
                }
            }
        } else {
            Scaffold {
                navHost(PaddingValues(0.dp), null)
            }
        }
    }
}