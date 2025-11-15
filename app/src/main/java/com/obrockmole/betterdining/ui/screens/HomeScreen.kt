package com.obrockmole.betterdining.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.GetStartLocationsQuery
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.repository.StartLocationsRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.HomeUiState
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.MenuViewModel
import com.obrockmole.betterdining.viewmodel.MenuViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val diningCourtOptions = listOf("Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor")
val quickBiteOptionsFormal =
    listOf("1bowl at Meredith Hall", "Pete's Za at Tarkington Hall", "Sushi Boss at Meredith Hall")

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String, String) -> Unit,
    viewModel: HomeViewModel
) {
    val selectedDiningCourtFromFav by viewModel.selectedDiningCourt.collectAsState()
    val selectedMealNameFromFav by viewModel.selectedMealName.collectAsState()
    val selectedDateFromFav by viewModel.selectedDate.collectAsState()

    var selectedFoodLocation by rememberSaveable { mutableStateOf<String?>(null) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedDiningCourtFromFav) {
        if (selectedDiningCourtFromFav != null) {
            selectedFoodLocation = selectedDiningCourtFromFav
        }

        val date = LocalDate.now()
        viewModel.getLocations(date.toString())
    }

    if (isSearchActive) {
        SearchScreen(
            onBack = { isSearchActive = false },
            homeViewModel = viewModel
        )
    } else if (selectedFoodLocation != null) {
        val menuViewModel: MenuViewModel = viewModel(
            key = selectedFoodLocation,
            factory = MenuViewModelFactory(MenuRepository())
        )
        Log.e("HomeScreen", "Selected food location: $selectedFoodLocation")
        FoodLocationDetail(
            name = when (selectedFoodLocation) {
                "1bowl at Meredith Hall" -> "1bowl"
                "Pete's Za at Tarkington Hall" -> "Pete's Za"
                "Sushi Boss at Meredith Hall" -> "Sushi Boss"
                else -> selectedFoodLocation!!
            },
            nameFormal = selectedFoodLocation!!,
            viewModel = menuViewModel,
            onBack = {
                selectedFoodLocation = null
                viewModel.clearNavigation()
            },
            onNavigateToItem = onNavigateToItem,
            initialMealName = selectedMealNameFromFav,
            initialDate = selectedDateFromFav
        )

    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            when (val uiState = viewModel.homeUiState) {
                is HomeUiState.Success -> {
                    val diningCourts =
                        uiState.data!!.filter { it.name == "Dining Courts" }[0].diningCourts
                    val quickBites =
                        uiState.data.filter { it.name == "Quick Bites" }[0].diningCourts

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Dining Courts",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                modifier = Modifier
                                    .clickable(onClick = { isSearchActive = true })
                                    .padding(16.dp),
                                contentDescription = "Search for item."
                            )
                        }
                    }
                    items(diningCourtOptions) { diningCourt ->
                        DiningCourtListItem(
                            diningCourt = diningCourts.first { it.name == diningCourt },
                            onClicked = {
                                selectedFoodLocation = diningCourt
                            }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Quick Bites",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                    items(quickBiteOptionsFormal) { quickBite ->
                        QuickBiteListItem(
                            quickBite = quickBites.first { it.name == quickBite },
                            onClicked = {
                                selectedFoodLocation = quickBite
                            }
                        )
                    }
                }

                else -> Log.e("HomeScreen", "HomeScreen: $uiState")
            }
        }
    }
}

@Composable
fun DiningCourtListItem(
    diningCourt: GetStartLocationsQuery.DiningCourt,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diningCourtIcon = when (diningCourt.name.lowercase()) {
        "earhart" -> R.drawable.earhart_icon
        "ford" -> R.drawable.ford_icon
        "hillenbrand" -> R.drawable.hillenbrand_icon
        "wiley" -> R.drawable.wiley_icon
        "windsor" -> R.drawable.windsor_icon
        else -> R.drawable.app_icon
    }

    val dailyMenu = diningCourt.dailyMenu!!
    var currentMealIndex = -1
    val currentHour = LocalDateTime.now().toLocalTime().hour
    dailyMenu.meals.forEachIndexed { index, meal ->
        if (meal.status.toString() == "OPEN") {
            val startTime = LocalDateTime.parse(
                meal.startTime,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            ).toLocalTime().hour
            val endTime = LocalDateTime.parse(
                meal.endTime,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            ).toLocalTime().hour

            if (currentHour in startTime until endTime) {
                currentMealIndex = index
                return@forEachIndexed
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClicked)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = diningCourtIcon),
                modifier = Modifier.size(80.dp),
                contentDescription = "Dining Court Icon"
            )

            Column {
                Text(
                    text = diningCourt.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = if (currentMealIndex >= 0) dailyMenu.meals[currentMealIndex].status.toString() else "Closed",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = "That time shit be comin, just wait",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.keyboard_arrow_right),
            contentDescription = "See menu."
        )
    }
    HorizontalDivider()
}


@Composable
fun QuickBiteListItem(
    quickBite: GetStartLocationsQuery.DiningCourt,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = when (quickBite.name) {
        "1bowl at Meredith Hall" -> "1bowl"
        "Pete's Za at Tarkington Hall" -> "Pete's Za"
        "Sushi Boss at Meredith Hall" -> "Sushi Boss"
        else -> quickBite.name
    }

    val quickBiteIcon = when (name.lowercase()) {
        "1bowl" -> R.drawable.onebowl_icon
        "pete's za" -> R.drawable.petes_icon
        "sushi boss" -> R.drawable.sushiboss_icon
        else -> R.drawable.app_icon
    }

    val dailyMenu = quickBite.dailyMenu!!
    var currentMealIndex = -1
    val currentHour = LocalDateTime.now().toLocalTime().hour
    dailyMenu.meals.forEachIndexed { index, meal ->
        if (meal.status.toString() == "OPEN") {
            val startTime = LocalDateTime.parse(
                meal.startTime,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            ).toLocalTime().hour
            val endTime = LocalDateTime.parse(
                meal.endTime,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            ).toLocalTime().hour

            if (currentHour in startTime until endTime) {
                currentMealIndex = index
                return@forEachIndexed
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClicked)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = quickBiteIcon),
                modifier = Modifier.size(64.dp),
                contentDescription = "Quick Bite Icon"
            )

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = if (currentMealIndex >= 0) dailyMenu.meals[currentMealIndex].status.toString() else "Closed",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = "That time shit be comin, just wait",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.keyboard_arrow_right),
            contentDescription = "See menu."
        )
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BetterPurdueDiningTheme {
        HomeScreen(
            onNavigateToItem = { _, _ -> },
            viewModel = HomeViewModel(
                StartLocationsRepository()
            )
        )
    }
}
