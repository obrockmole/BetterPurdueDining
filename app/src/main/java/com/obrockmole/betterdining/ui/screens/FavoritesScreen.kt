package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemShapes
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.StartLocationsRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.FavoritesViewModel
import com.obrockmole.betterdining.viewmodel.FavoritesViewModelFactory
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String, String) -> Unit,
    homeViewModel: HomeViewModel,
    showHeader: Boolean
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "All Favorites")
    var selectedSort by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (showHeader) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Favorites",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.headlineMedium
                )

                if (tabIndex == 1) {
                    var sortMenuShown by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sort),
                            modifier = Modifier
                                .clickable(onClick = { sortMenuShown = true }),
                            contentDescription = "Sort favorites."
                        )

                        DropdownMenuPopup(
                            expanded = sortMenuShown,
                            onDismissRequest = { sortMenuShown = false }
                        ) {
                            DropdownMenuGroup(
                                shapes = MenuDefaults.groupShape(0, 1)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Name") },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.keyboard_arrow_up),
                                            contentDescription = "Increasing name."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 0
                                    },
                                    selected = selectedSort == 0,
                                    shapes = MenuItemShapes(
                                        MenuDefaults.leadingItemShape,
                                        MenuDefaults.selectedItemShape
                                    )
                                )
                                DropdownMenuItem(
                                    text = { Text("Name") },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.keyboard_arrow_down),
                                            contentDescription = "Decreasing name."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 1
                                    },
                                    selected = selectedSort == 1,
                                    shapes = MenuItemShapes(
                                        MenuDefaults.middleItemShape,
                                        MenuDefaults.selectedItemShape
                                    )
                                )

                                HorizontalDivider(
                                    modifier =
                                        Modifier.padding(horizontal = MenuDefaults.HorizontalDividerPadding)
                                )

                                DropdownMenuItem(
                                    text = { Text("Date Added") },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.keyboard_arrow_up),
                                            contentDescription = "Increasing date added."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 2
                                    },
                                    selected = selectedSort == 2,
                                    shapes = MenuItemShapes(
                                        MenuDefaults.middleItemShape,
                                        MenuDefaults.selectedItemShape
                                    )
                                )
                                DropdownMenuItem(
                                    text = { Text("Date Added") },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.keyboard_arrow_down),
                                            contentDescription = "Decreasing date added."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 3
                                    },
                                    selected = selectedSort == 3,
                                    shapes = MenuItemShapes(
                                        MenuDefaults.trailingItemShape,
                                        MenuDefaults.selectedItemShape
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        SecondaryTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }

        when (tabIndex) {
            0 -> UpcomingFavoritesScreen(homeViewModel = homeViewModel)
            1 -> AllFavoritesList(onNavigateToItem = onNavigateToItem, selectedSort = selectedSort)
        }
    }
}

@Composable
fun AllFavoritesList(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String, String) -> Unit,
    selectedSort: Int
) {
    val context = LocalContext.current
    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(
            FavoritesRepository(AppDatabase.getDatabase(context).favoriteItemDao())
        )
    )

    val favorites by favoritesViewModel.favorites.collectAsState()
    var sortedFavorites = favorites.sortedBy { it.name }
    if (selectedSort == 1) {
        sortedFavorites = sortedFavorites.reversed()
    } else if (selectedSort == 2 || selectedSort == 3) {
        sortedFavorites = sortedFavorites.sortedBy {
            OffsetDateTime.parse(
                it.dateAdded,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            ).toLocalDateTime()
        }
        if (selectedSort == 3) {
            sortedFavorites = sortedFavorites.reversed()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (sortedFavorites.isEmpty()) {
            Text(
                text = "No items favorited.",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sortedFavorites) { favoriteItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                onNavigateToItem(
                                    favoriteItem.name,
                                    favoriteItem.itemId
                                )
                            })
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = favoriteItem.name)
                        Icon(
                            painter = painterResource(R.drawable.keyboard_arrow_right),
                            contentDescription = "Go to item."
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    BetterPurdueDiningTheme {
        FavoritesScreen(
            onNavigateToItem = { _, _ -> },
            homeViewModel = HomeViewModel(
                StartLocationsRepository()
            ),
            showHeader = true
        )
    }
}