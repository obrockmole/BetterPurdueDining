package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.utils.Logger
import kmpbetterdining.shared.generated.resources.Res
import kmpbetterdining.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "FavoritesScreen"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "All Favorites")
    var selectedSort by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        val showHeader = true
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
                    style = MaterialTheme.typography.headlineMediumEmphasized
                )

                if (tabIndex == 1) {
                    var sortMenuShown by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.sort),
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
                                            painter = painterResource(Res.drawable.keyboard_arrow_up),
                                            contentDescription = "Increasing name."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 0
                                        Logger.LogDebug(LOG_TAG, "Sorting by name ascending")
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
                                            painter = painterResource(Res.drawable.keyboard_arrow_down),
                                            contentDescription = "Decreasing name."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 1
                                        Logger.LogDebug(LOG_TAG, "Sorting by name descending")
                                    },
                                    selected = selectedSort == 1,
                                    shapes = MenuItemShapes(
                                        MenuDefaults.middleItemShape,
                                        MenuDefaults.selectedItemShape
                                    )
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                                )

                                DropdownMenuItem(
                                    text = { Text("Date Added") },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(Res.drawable.keyboard_arrow_up),
                                            contentDescription = "Increasing date added."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 2
                                        Logger.LogDebug(LOG_TAG, "Sorting by date ascending")
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
                                            painter = painterResource(Res.drawable.keyboard_arrow_down),
                                            contentDescription = "Decreasing date added."
                                        )
                                    },
                                    onClick = {
                                        sortMenuShown = false
                                        selectedSort = 3
                                        Logger.LogDebug(LOG_TAG, "Sorting by date descending")
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
                    onClick = {
                        Logger.LogDebug(LOG_TAG, "Switched to $title")
                        tabIndex = index
                    }
                )
            }
        }

        when (tabIndex) {
            0 -> UpcomingFavoritesScreen()
            1 -> AllFavoritesList()
        }
    }
}

@Composable
fun AllFavoritesList(
    modifier: Modifier = Modifier
) {
    val favorites = emptyList<String>()

    Box(modifier = modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
            Text(
                text = "No items favorited.",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favorites) { favoriteItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
//                                Logger.LogInfo(LOG_TAG, "Navigating to item ${favoriteItem.name} (${favoriteItem.itemId})")
                            })
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
//                        Text(text = favoriteItem.name)
                        Text(text = "Banana")
                        Icon(
                            painter = painterResource(Res.drawable.keyboard_arrow_right),
                            contentDescription = "Go to item."
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}