package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.GetStartLocationsQuery
import com.obrockmole.kmpbetterdining.type.MealStatus
import com.obrockmole.kmpbetterdining.utils.DateTime
import com.obrockmole.kmpbetterdining.utils.Logger
import com.obrockmole.kmpbetterdining.viewmodel.DiningCourtWithCustomName
import com.obrockmole.kmpbetterdining.viewmodel.HomeUiState
import com.obrockmole.kmpbetterdining.viewmodel.HomeViewModel
import kmpbetterdining.shared.generated.resources.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Instant

private const val LOG_TAG = "HomeScreen"

val diningCourtOptions = listOf("Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor")
val quickBiteOptionsFormal =
    listOf("1bowl at Meredith Hall", "Pete's Za at Tarkington Hall", "Sushi Boss at South Hall")

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

        val date = DateTime.getDate()
        Logger.LogDebug(LOG_TAG, "Getting locations for date: $date")
        viewModel.getLocations(date.toString())
    }

    when (val uiState = viewModel.homeUiState) {
        is HomeUiState.Loading -> {
            Logger.LogDebug(LOG_TAG, "UI loading")
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is HomeUiState.Error -> {
            Logger.LogError(LOG_TAG, "Failed to load UI: ${uiState.message}")
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Error loading locations.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        is HomeUiState.Success -> {
            Logger.LogDebug(LOG_TAG, "UI loaded successfully")
            LazyColumn(modifier = modifier.fillMaxSize()) {
                val diningCourts = uiState.data!!.first { it.first == "Dining Courts" }.second
                val quickBites = uiState.data.first { it.first == "Quick Bites" }.second

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
                            painter = painterResource(Res.drawable.search),
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
                    val diningCourt = diningCourts.first { it.diningCourt.name == diningCourtName }

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
    }
}

@Composable
fun DiningCourtListItem(
    diningCourt: DiningCourtWithCustomName,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diningCourtIcon = when (diningCourt.diningCourt.name.lowercase()) {
        "earhart" -> Res.drawable.earhart_icon
        "ford" -> Res.drawable.ford_icon
        "hillenbrand" -> Res.drawable.hillenbrand_icon
        "wiley" -> Res.drawable.wiley_icon
        "windsor" -> Res.drawable.windsor_icon
        else -> Res.drawable.app_icon
    }

    val dailyMenu = diningCourt.diningCourt.dailyMenu!!
    var currentMealIndex = -1
    val currentHour = DateTime.getTime().hour
    dailyMenu.meals.forEachIndexed { index, meal ->
        if (meal.status == MealStatus.OPEN) {
            val startTime = DateTime.parseTime(meal.startTime!!).hour
            val endTime = DateTime.parseTime(meal.endTime!!).hour

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
                painter = painterResource(diningCourtIcon),
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
                    val endTimeInstant = dailyMenu.meals[currentMealIndex].endTime?.let { Instant.parse(it) }
                    val localTime = endTimeInstant!!.toLocalDateTime(TimeZone.currentSystemDefault()).time

                    Text(
                        text = "Serving " + dailyMenu.meals[currentMealIndex].name + " until " + localTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Icon(
            painter = painterResource(Res.drawable.keyboard_arrow_right),
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
        "Sushi Boss at South Hall" -> "Sushi Boss"
        else -> quickBite.diningCourt.name
    }

    val quickBiteIcon = when (name.lowercase()) {
        "1bowl" -> Res.drawable.onebowl_icon
        "pete's za" -> Res.drawable.petes_icon
        "sushi boss" -> Res.drawable.sushiboss_icon
        else -> Res.drawable.app_icon
    }

    val dailyMenu = quickBite.diningCourt.dailyMenu!!
    var currentMealIndex = -1
    val currentHour = DateTime.getTime().hour
    dailyMenu.meals.forEachIndexed { index, meal ->
        if (meal.status == MealStatus.OPEN) {
            val startTime = DateTime.parseTime(meal.startTime!!).hour
            val endTime = DateTime.parseTime(meal.endTime!!).hour

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
                painter = painterResource(quickBiteIcon),
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
                    val endTimeInstant = dailyMenu.meals[currentMealIndex].endTime?.let { Instant.parse(it) }
                    val localTime = endTimeInstant!!.toLocalDateTime(TimeZone.currentSystemDefault()).time

                    Text(
                        text = "Serving " + dailyMenu.meals[currentMealIndex].name + " until " + localTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Icon(
            painter = painterResource(Res.drawable.keyboard_arrow_right),
            contentDescription = "View menu."
        )
    }
    HorizontalDivider()
}
