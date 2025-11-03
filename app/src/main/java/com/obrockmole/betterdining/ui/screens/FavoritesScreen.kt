package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.viewmodel.FavoritesViewModel
import com.obrockmole.betterdining.viewmodel.FavoritesViewModelFactory
import com.obrockmole.betterdining.viewmodel.HomeViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String) -> Unit,
    homeViewModel: HomeViewModel
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "All Favorites")

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> UpcomingFavoritesScreen(homeViewModel = homeViewModel)
            1 -> AllFavoritesList(onNavigateToItem = onNavigateToItem)
        }
    }
}

@Composable
fun AllFavoritesList(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String) -> Unit
) {
    val context = LocalContext.current
    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(
            FavoritesRepository(AppDatabase.getDatabase(context).favoriteItemDao())
        )
    )

    val favorites by favoritesViewModel.favorites.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
            Text(
                text = "No items favorited.",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favorites) { favoriteItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        TextButton(
                            onClick = { onNavigateToItem(favoriteItem.itemId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = favoriteItem.name, modifier = Modifier.padding(16.dp))
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Go to item.")
                            }
                        }
                    }
                }
            }
        }
    }
}
