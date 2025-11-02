package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.viewmodel.ItemUiState
import com.obrockmole.betterdining.viewmodel.ItemViewModel
import com.obrockmole.betterdining.viewmodel.ItemViewModelFactory

@Composable
fun ItemDetailScreen(
    itemId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val itemViewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(
            MenuRepository(),
            FavoritesRepository(AppDatabase.getDatabase(context).favoriteItemDao())
        )
    )

    LaunchedEffect(itemId) {
        itemViewModel.getItem(itemId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val uiState = itemViewModel.itemUiState) {
            is ItemUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ItemUiState.Error -> {
                Text("Error loading item details", modifier = Modifier.align(Alignment.Center))
            }
            is ItemUiState.Success -> {
                val item = uiState.item
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = item.name, style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Ingredients: ${item.ingredients ?: "Not available"}")
                }

                val isFavorite = itemViewModel.isFavorite
                FloatingActionButton(
                    onClick = { itemViewModel.toggleFavorite(item) },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
                    )
                }
            }
        }
    }
}
