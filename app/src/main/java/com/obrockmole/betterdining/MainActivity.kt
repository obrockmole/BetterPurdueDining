package com.obrockmole.betterdining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.repository.RenamedCourtsRepository
import com.obrockmole.betterdining.repository.StartLocationsRepository
import com.obrockmole.betterdining.repository.UserPreferencesRepository
import com.obrockmole.betterdining.ui.screens.DefaultScreenSelectionScreen
import com.obrockmole.betterdining.ui.screens.FavoritesScreen
import com.obrockmole.betterdining.ui.screens.HomeScreen
import com.obrockmole.betterdining.ui.screens.ItemDetailScreen
import com.obrockmole.betterdining.ui.screens.LicensesScreen
import com.obrockmole.betterdining.ui.screens.NavStyleSelectionScreen
import com.obrockmole.betterdining.ui.screens.SettingsScreen
import com.obrockmole.betterdining.ui.screens.ThemeSelectionScreen
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetterPurdueDiningApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetterPurdueDiningApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }
    val appTheme by userPreferencesRepository.appTheme.collectAsState(initial = "Material")

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var isInitialScreenSet by rememberSaveable { mutableStateOf(false) }

    BetterPurdueDiningTheme(theme = appTheme, key = currentRoute to currentDestination) {
        val defaultScreen by userPreferencesRepository.defaultScreen.collectAsState(initial = null)
        val navStyle by userPreferencesRepository.navStyle.collectAsState(initial = null)
        val homeViewModel: HomeViewModel = viewModel(
            factory = HomeViewModelFactory(
                StartLocationsRepository(),
                RenamedCourtsRepository(AppDatabase.getDatabase(context).renamedDiningCourtDao())
            )
        )

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
                                                    painter = painterResource(it.painterId),
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
                                    when (currentDestination) {
                                        AppDestinations.HOME -> {
                                            HomeScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToItem = { itemName, itemId ->
                                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "itemName",
                                                        itemName
                                                    )
                                                    navController.navigate("item/$itemId")
                                                },
                                                viewModel = homeViewModel
                                            )
                                        }

                                        AppDestinations.FAVORITES -> {
                                            FavoritesScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToItem = { itemName, itemId ->
                                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "itemName",
                                                        itemName
                                                    )
                                                    navController.navigate("item/$itemId")
                                                },
                                                homeViewModel = homeViewModel,
                                                showHeader = true
                                            )
                                        }

                                        AppDestinations.SETTINGS -> {
                                            SettingsScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToDefaultScreen = {
                                                    navController.navigate("settings/defaultScreen")
                                                },
                                                onNavigateToTheme = {
                                                    navController.navigate("settings/theme")
                                                },
                                                onNavigateToNavStyle = {
                                                    navController.navigate("settings/navStyle")
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
                                                    painter = painterResource(id = R.drawable.app_icon),
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
                                                        painter = painterResource(destination.painterId),
                                                        contentDescription = destination.label
                                                    )
                                                },
                                                label = { Text(destination.label) },
                                                selected = destination == currentDestination,
                                                onClick = {
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
                                                        drawerState.open()
                                                    }
                                                }) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.menu),
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
                                                onNavigateToItem = { itemName, itemId ->
                                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "itemName",
                                                        itemName
                                                    )
                                                    navController.navigate("item/$itemId")
                                                },
                                                viewModel = homeViewModel
                                            )
                                        }

                                        AppDestinations.FAVORITES -> {
                                            FavoritesScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToItem = { itemName, itemId ->
                                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "itemName",
                                                        itemName
                                                    )
                                                    navController.navigate("item/$itemId")
                                                },
                                                homeViewModel = homeViewModel,
                                                showHeader = false
                                            )
                                        }

                                        AppDestinations.SETTINGS -> {
                                            SettingsScreen(
                                                modifier = Modifier.padding(innerPadding),
                                                onNavigateToDefaultScreen = {
                                                    navController.navigate("settings/defaultScreen")
                                                },
                                                onNavigateToTheme = {
                                                    navController.navigate("settings/theme")
                                                },
                                                onNavigateToNavStyle = {
                                                    navController.navigate("settings/navStyle")
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
                }
            }

            composable(
                "item/{itemId}",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                val itemName =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>("itemName")
                        ?: ""
                if (itemId != null) {
                    ItemDetailScreen(
                        itemName = itemName,
                        itemId = itemId,
                        onNavigateBack = { navController.popBackStack() },
                        homeViewModel = homeViewModel
                    )
                }
            }

            composable(
                "settings/defaultScreen",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                DefaultScreenSelectionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                "settings/theme",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                ThemeSelectionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                "settings/navStyle",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                NavStyleSelectionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                "settings/licenses",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                LicensesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val painterId: Int,
) {
    HOME("Home", R.drawable.home),
    FAVORITES("Favorites", R.drawable.favorite),
    SETTINGS("Settings", R.drawable.settings)
}


@Preview(showBackground = true)
@Composable
fun BetterPurdueDiningPreview() {
    BetterPurdueDiningApp()
}
