package com.obrockmole.betterdining.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.obrockmole.betterdining.GetLocationMenuQuery
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.viewmodel.MenuUiState
import com.obrockmole.betterdining.viewmodel.MenuViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLocationDetail(
    name: String,
    nameFormal: String,
    viewModel: MenuViewModel,
    onBack: () -> Unit,
    onNavigateToItem: (String, String) -> Unit,
    initialMealName: String?,
    initialDate: String?
) {
    BackHandler {
        onBack()
    }

    LaunchedEffect(nameFormal, initialDate) {
        val date = initialDate?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toString()
        }
        viewModel.getMenu(nameFormal, date)
    }

    var selectedMealIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                        val menuData = uiState.data
                        val meals = menuData.dailyMenu!!.meals

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
                                SecondaryTabRow(
                                    selectedTabIndex = selectedMealIndex,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
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
                                        "No meal is being served.",
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
                            Text(
                                "No meal is being served.",
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealDetail(
    meal: GetLocationMenuQuery.Meal,
    onNavigateToItem: (String, String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(meal.stations) { station ->
            StationDetail(station = station, onNavigateToItem = onNavigateToItem)
        }
    }
}

@Composable
fun StationDetail(
    station: GetLocationMenuQuery.Station,
    modifier: Modifier = Modifier,
    onNavigateToItem: (String, String) -> Unit
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(text = station.name, style = MaterialTheme.typography.titleLarge)
        station.items.forEach { itemWrapper ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        onNavigateToItem(
                            itemWrapper.item.name,
                            itemWrapper.item.itemId
                        )
                    })
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = itemWrapper.item.name)
            }
            HorizontalDivider()
        }
    }
}
