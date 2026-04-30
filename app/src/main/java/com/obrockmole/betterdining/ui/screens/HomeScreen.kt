package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.unit.dp
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.type.MealStatus
import com.obrockmole.betterdining.utils.Logger
import com.obrockmole.betterdining.viewmodel.DiningCourtWithCustomName
import com.obrockmole.betterdining.viewmodel.HomeUiState
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val LOG_TAG = "HomeScreen"

val diningCourtOptions = listOf("Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor")
val quickBiteOptionsFormal =
    listOf("1bowl at Meredith Hall", "Pete's Za at Tarkington Hall", "Sushi Boss at Meredith Hall")

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToFoodLocation: (String, String) -> Unit,
    viewModel: HomeViewModel
) {
    val selectedDiningCourtFromFav by viewModel.selectedDiningCourt.collectAsState()
    Logger.LogDebug(LOG_TAG, "selectedDiningCourtFromFav: ${selectedDiningCourtFromFav.first}")

    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedDiningCourtFromFav) {
        if (selectedDiningCourtFromFav.first != null && selectedDiningCourtFromFav.second != null) {
            Logger.LogInfo(LOG_TAG, "Navigating to dining court from favorites: ${selectedDiningCourtFromFav.first}")
            onNavigateToFoodLocation(
                selectedDiningCourtFromFav.first!!,
                selectedDiningCourtFromFav.second!!
            )
            viewModel.clearNavigation()
        }

        val date = LocalDate.now()
        Logger.LogDebug(LOG_TAG, "Getting locations for date: $date")
        viewModel.getLocations(date.toString())
    }

    if (isSearchActive) {
        Logger.LogDebug(LOG_TAG, "Activated search")
        SearchScreen(
            onBack = {
                Logger.LogDebug(LOG_TAG, "Exited search")
                isSearchActive = false
            },
            homeViewModel = viewModel
        )

    } else {
        when (val uiState = viewModel.homeUiState) {
            is HomeUiState.Loading -> {
                Logger.LogDebug(LOG_TAG, "UI loading")
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is HomeUiState.Success -> {
                Logger.LogDebug(LOG_TAG, "UI loaded successfully")
                LazyColumn(modifier = modifier.fillMaxSize()) {
                    val diningCourts =
                        uiState.data!!.first { it.first == "Dining Courts" }.second
                    val quickBites =
                        uiState.data.first { it.first == "Quick Bites" }.second

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
                                style = MaterialTheme.typography.headlineMediumEmphasized
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                modifier = Modifier
                                    .clickable(onClick = {
                                        Logger.LogDebug(LOG_TAG, "Activating search attempt")
                                        isSearchActive = true
                                    })
                                    .padding(16.dp),
                                contentDescription = "Search for item."
                            )
                        }
                    }
                    items(diningCourtOptions) { diningCourtName ->
                        val diningCourt =
                            diningCourts.first { it.diningCourt.name == diningCourtName }
                        DiningCourtListItem(
                            diningCourt = diningCourt,
                            onClicked = {
                                Logger.LogInfo(LOG_TAG, "Navigating to dining court: ${diningCourt.diningCourt.name}")
                                onNavigateToFoodLocation(
                                    diningCourt.diningCourt.name,
                                    diningCourt.diningCourt.id
                                )
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
                                style = MaterialTheme.typography.headlineMediumEmphasized
                            )
                        }
                    }
                    items(quickBiteOptionsFormal) { quickBiteName ->
                        val quickBite = quickBites.first { it.diningCourt.name == quickBiteName }
                        QuickBiteListItem(
                            quickBite = quickBite,
                            onClicked = {
                                val name = when (quickBite.diningCourt.name) {
                                    "1bowl at Meredith Hall" -> "1bowl"
                                    "Pete's Za at Tarkington Hall" -> "Pete's Za"
                                    "Sushi Boss at Meredith Hall" -> "Sushi Boss"
                                    else -> quickBite.diningCourt.name
                                }
                                Logger.LogInfo(LOG_TAG, "Navigating to quick bite: $name")
                                onNavigateToFoodLocation(
                                    name,
                                    quickBite.diningCourt.id
                                )
                            }
                        )
                    }
                }
            }

            else -> Logger.LogError(LOG_TAG, "Error loading UI. State: $uiState")
        }
    }
}

@Composable
fun DiningCourtListItem(
    diningCourt: DiningCourtWithCustomName,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diningCourtIcon = when (diningCourt.diningCourt.name.lowercase()) {
        "earhart" -> R.drawable.earhart_icon
        "ford" -> R.drawable.ford_icon
        "hillenbrand" -> R.drawable.hillenbrand_icon
        "wiley" -> R.drawable.wiley_icon
        "windsor" -> R.drawable.windsor_icon
        else -> R.drawable.app_icon
    }

    val dailyMenu = diningCourt.diningCourt.dailyMenu!!
    var currentMealIndex = -1
    val currentHour = LocalDateTime.now(ZoneId.of("America/New_York")).toLocalTime().hour
    dailyMenu.meals.forEachIndexed { index, meal ->
        if (meal.status == MealStatus.OPEN) {
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
                Logger.LogDebug(LOG_TAG, "DiningCourtListItem: ${diningCourt.diningCourt.name} OPEN. Current meal ${meal.name} ($currentMealIndex)")
                return@forEachIndexed
            }
        }
    }

    if (currentMealIndex == -1) {
        Logger.LogDebug(LOG_TAG, "DiningCourtListItem: ${diningCourt.diningCourt.name} CLOSED")
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
                    text = diningCourt.customName ?: diningCourt.diningCourt.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = if (currentMealIndex >= 0) dailyMenu.meals[currentMealIndex].status.toString()
                        .lowercase().replaceFirstChar { it.uppercase() } else "Closed",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                if (currentMealIndex >= 0) {
                    Text(
                        text = "Serving " + dailyMenu.meals[currentMealIndex].name + " until " +
                                OffsetDateTime.parse(
                                    dailyMenu.meals[currentMealIndex].endTime,
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                ).atZoneSameInstant(ZoneId.systemDefault())
                                    .toLocalTime()
                                    .format(DateTimeFormatter.ofPattern("h:mm a")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
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
    quickBite: DiningCourtWithCustomName,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = when (quickBite.diningCourt.name) {
        "1bowl at Meredith Hall" -> "1bowl"
        "Pete's Za at Tarkington Hall" -> "Pete's Za"
        "Sushi Boss at Meredith Hall" -> "Sushi Boss"
        else -> quickBite.diningCourt.name
    }

    val quickBiteIcon = when (name.lowercase()) {
        "1bowl" -> R.drawable.onebowl_icon
        "pete's za" -> R.drawable.petes_icon
        "sushi boss" -> R.drawable.sushiboss_icon
        else -> R.drawable.app_icon
    }

    val dailyMenu = quickBite.diningCourt.dailyMenu!!
    var currentMealIndex = -1
    val currentHour = LocalDateTime.now(ZoneId.of("America/New_York")).toLocalTime().hour
    dailyMenu.meals.forEachIndexed { index, meal ->
        if (meal.status == MealStatus.OPEN) {
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
                Logger.LogDebug(LOG_TAG, "QuickBiteListItem: ${quickBite.diningCourt.name} OPEN. Current meal ${meal.name} ($currentMealIndex)")
                return@forEachIndexed
            }
        }
    }

    if (currentMealIndex == -1) {
        Logger.LogDebug(LOG_TAG, "QuickBiteListItem: ${quickBite.diningCourt.name} CLOSED")
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
                modifier = Modifier.size(80.dp),
                contentDescription = "Quick Bite Icon"
            )

            Column {
                Text(
                    text = quickBite.customName ?: name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = if (currentMealIndex >= 0) dailyMenu.meals[currentMealIndex].status.toString()
                        .lowercase().replaceFirstChar { it.uppercase() } else "Closed",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                if (currentMealIndex >= 0) {
                    Text(
                        text = "Serving " + dailyMenu.meals[currentMealIndex].name + " until " +
                                OffsetDateTime.parse(
                                    dailyMenu.meals[currentMealIndex].endTime,
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                ).atZoneSameInstant(ZoneId.systemDefault())
                                    .toLocalTime()
                                    .format(DateTimeFormatter.ofPattern("h:mm a")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Icon(
            painter = painterResource(R.drawable.keyboard_arrow_right),
            contentDescription = "View menu."
        )
    }
    HorizontalDivider()
}
