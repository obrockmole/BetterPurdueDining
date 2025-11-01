package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.viewmodel.FavoritesViewModel
import com.obrockmole.betterdining.viewmodel.FavoritesViewModelFactory

@Composable
fun FavoritesScreen(
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
                text = "No favorites yet!",
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
                            Text(text = favoriteItem.name, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}