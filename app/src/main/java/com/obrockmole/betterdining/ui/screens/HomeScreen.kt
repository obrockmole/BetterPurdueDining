package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.models.Meal
import com.obrockmole.betterdining.models.Station
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.viewmodel.MenuUiState
import com.obrockmole.betterdining.viewmodel.MenuViewModel
import com.obrockmole.betterdining.viewmodel.MenuViewModelFactory

val diningCourtOptions = listOf("Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor")

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var selectedDiningCourt by rememberSaveable { mutableStateOf<String?>(null) }

    if (selectedDiningCourt != null) {
        val menuViewModel: MenuViewModel = viewModel(
            factory = MenuViewModelFactory(MenuRepository())
        )
        DiningCourtDetail(
            diningCourtName = selectedDiningCourt!!,
            viewModel = menuViewModel,
            onBack = { selectedDiningCourt = null }
        )
    } else {
        DiningCourtList(
            diningCourts = diningCourtOptions,
            onDiningCourtClicked = { selectedDiningCourt = it },
            modifier = modifier
        )
    }
}

@Composable
fun DiningCourtList(
    diningCourts: List<String>,
    onDiningCourtClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(diningCourts) { diningCourt ->
            DiningCourtListItem(
                diningCourtName = diningCourt,
                onClicked = { onDiningCourtClicked(diningCourt) }
            )
        }
    }
}

@Composable
fun DiningCourtListItem(
    diningCourtName: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClicked)
    ) {
        Text(
            text = diningCourtName,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun DiningCourtDetail(diningCourtName: String, viewModel: MenuViewModel, onBack: () -> Unit) {
    LaunchedEffect(diningCourtName) {
        viewModel.getMenu(diningCourtName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val uiState = viewModel.menuUiState) {
            is MenuUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is MenuUiState.Error -> {
                Text("Error loading menu", modifier = Modifier.align(Alignment.Center))
            }
            is MenuUiState.Success -> {
                MealDetail(meal = uiState.meal)
            }
        }
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}

@Composable
fun MealDetail(meal: Meal, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        item {
            Text(text = meal.name, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        }
        items(meal.stations) { station ->
            StationDetail(station = station)
        }
    }
}

@Composable
fun StationDetail(station: Station, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(text = station.name, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        station.items.forEach { itemWrapper ->
            Text(text = itemWrapper.item.name, modifier = Modifier.padding(start = 16.dp))
        }
    }
}
