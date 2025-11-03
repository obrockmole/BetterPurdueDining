package com.obrockmole.betterdining.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.models.Meal
import com.obrockmole.betterdining.models.Station
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.MenuUiState
import com.obrockmole.betterdining.viewmodel.MenuViewModel
import com.obrockmole.betterdining.viewmodel.MenuViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val diningCourtOptions = listOf("Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor")

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val selectedDiningCourtFromFav by viewModel.selectedDiningCourt.collectAsState()
    val selectedMealNameFromFav by viewModel.selectedMealName.collectAsState()
    val selectedDateFromFav by viewModel.selectedDate.collectAsState()

    var selectedDiningCourt by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedDiningCourtFromFav) {
        selectedDiningCourt = selectedDiningCourtFromFav
    }

    if (selectedDiningCourt != null) {
        val menuViewModel: MenuViewModel = viewModel(
            factory = MenuViewModelFactory(MenuRepository())
        )
        DiningCourtDetail(
            diningCourtName = selectedDiningCourt!!,
            viewModel = menuViewModel,
            onBack = {
                selectedDiningCourt = null
                viewModel.clearNavigation()
            },
            onNavigateToItem = onNavigateToItem,
            initialMealName = selectedMealNameFromFav,
            initialDate = selectedDateFromFav
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = diningCourtName,
                modifier = Modifier.padding(16.dp)
            )
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "See menu.")
        }
    }
}

@Composable
fun DiningCourtDetail(
    diningCourtName: String,
    viewModel: MenuViewModel,
    onBack: () -> Unit,
    onNavigateToItem: (String) -> Unit,
    initialMealName: String?,
    initialDate: String?
) {
    BackHandler {
        onBack()
    }

    LaunchedEffect(diningCourtName, initialDate) {
        val date = initialDate?.let {
            java.time.LocalDate.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toString()
        }
        viewModel.getMenu(diningCourtName, date)
    }

    var selectedMealIndex by rememberSaveable { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (val uiState = viewModel.menuUiState) {
                is MenuUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is MenuUiState.Error -> {
                    AlertDialog(
                        onDismissRequest = onBack,
                        title = { Text(text = "Oopsie Poopsie") },
                        text = { Text(text = "Purdue did a fucky wucky.") },
                        confirmButton = {
                            TextButton(onClick = onBack) {
                                Text("God Damnit.")
                            }
                        }
                    )
                }

                is MenuUiState.Success -> {
                    val meals = uiState.meals

                    LaunchedEffect(meals, initialMealName) {
                        if (initialMealName != null) {
                            val index = meals.indexOfFirst { it.name == initialMealName }
                            if (index != -1) {
                                selectedMealIndex = index
                            }
                        } else {
                            val currentHour = LocalDateTime.now().toLocalTime().hour
                            meals.forEachIndexed { index, meal ->
                                if (!meal.stations.isEmpty()) {
                                    val startTime = LocalDateTime.parse(
                                        meal.startTime,
                                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                    ).toLocalTime().hour
                                    val endTime = LocalDateTime.parse(
                                        meal.endTime,
                                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                    ).toLocalTime().hour

                                    if (currentHour in startTime until endTime) {
                                        selectedMealIndex = index
                                        return@forEachIndexed
                                    }
                                }
                            }
                        }
                    }

                    if (meals.isNotEmpty()) {
                        Column {
                            TabRow(selectedTabIndex = selectedMealIndex, modifier = Modifier.fillMaxWidth().padding(top = 48.dp)) {
                                meals.forEachIndexed { index, meal ->
                                    Tab(
                                        selected = selectedMealIndex == index,
                                        onClick = { selectedMealIndex = index },
                                        text = { Text(meal.name) }
                                    )
                                }
                            }
                            if (meals[selectedMealIndex].stations.isEmpty()) {
                                Text(
                                    "No meals available.",
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(16.dp)
                                )
                            } else {
                                MealDetail(
                                    meal = meals[selectedMealIndex],
                                    onNavigateToItem = onNavigateToItem
                                )
                            }
                        }
                    } else {
                        Text("No meals available.", modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }
}

@Composable
fun MealDetail(
    meal: Meal,
    modifier: Modifier = Modifier,
    onNavigateToItem: (String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(text = meal.name, style = MaterialTheme.typography.headlineMedium)
        }
        items(meal.stations) { station ->
            StationDetail(station = station, onNavigateToItem = onNavigateToItem)
        }
    }
}

@Composable
fun StationDetail(
    station: Station,
    modifier: Modifier = Modifier,
    onNavigateToItem: (String) -> Unit
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(text = station.name, style = MaterialTheme.typography.titleLarge)
        station.items.forEach { itemWrapper ->
            TextButton(onClick = { onNavigateToItem(itemWrapper.item.itemId) }) {
                Text(text = itemWrapper.item.name, modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BetterPurdueDiningTheme {
        HomeScreen(onNavigateToItem = {}, viewModel = HomeViewModel())
    }
}
