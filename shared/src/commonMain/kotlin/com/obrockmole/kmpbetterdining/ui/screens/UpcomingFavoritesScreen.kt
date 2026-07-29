package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.models.Appearance
import com.obrockmole.kmpbetterdining.utils.DateTime
import com.obrockmole.kmpbetterdining.utils.DiningCourtIdMap
import com.obrockmole.kmpbetterdining.utils.Logger
import com.obrockmole.kmpbetterdining.viewmodel.HomeViewModel
import com.obrockmole.kmpbetterdining.viewmodel.UpcomingFavoritesViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.format
import kotlinx.datetime.plus

private const val LOG_TAG = "UpcomingFavoritesScreen"

@Composable
fun UpcomingFavoritesScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    upcomingFavoritesViewModel: UpcomingFavoritesViewModel
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val upcomingFavoritesResult by upcomingFavoritesViewModel.upcomingFavorites.collectAsState()
    var showMore by remember { mutableStateOf(false) }

    upcomingFavoritesResult.fold(
        onSuccess = { upcomingFavorites ->
            if (upcomingFavorites.all { it.appearances.isEmpty() }) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No upcoming favorites this next week.")
                }
                Logger.LogDebug(LOG_TAG, "No upcoming favorites found this week")
                return@fold
            }

            val today = DateTime.getDate()

            val allAppearances = upcomingFavorites.flatMap { favorite ->
                favorite.appearances.map { appearance -> favorite.name to appearance }
            }.distinctBy { (name, appearance) ->
                Triple(name, appearance.locationName, appearance.date)
            }.sortedBy { (_, appearance) ->
                DateTime.parseDateTime(appearance.date)
            }

            val todayAppearances = allAppearances.filter { (_, appearance) ->
                DateTime.parseDate(appearance.date) == today
            }

            val weekAppearances = allAppearances.filter { (_, appearance) ->
                val date = DateTime.parseDate(appearance.date)
                (date > today) && (date < today.plus(7, DateTimeUnit.DayBased(1)))
            }

            val groupedAppearances = if (showMore || todayAppearances.isEmpty()) {
                (todayAppearances + weekAppearances).groupBy { (_, appearance) ->
                    DateTime.parseDate(appearance.date)
                }
            } else {
                todayAppearances.groupBy { (_, appearance) ->
                    DateTime.parseDate(appearance.date)
                }
            }

            Logger.LogDebug(LOG_TAG, "Loaded ${allAppearances.size} total appearances, ${todayAppearances.size} today, ${weekAppearances.size} this week")


            if (groupedAppearances.isEmpty()) {
                val message = if (showMore) "No upcoming favorites found for the next week." else "Nothing available today."
                Logger.LogDebug(LOG_TAG, message)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = message, modifier = Modifier.padding(16.dp))
                }

            } else {
                Box(modifier = modifier.fillMaxSize()) {
                    Column(modifier = modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = if (weekAppearances.isNotEmpty()) {
                                PaddingValues(bottom = 72.dp)
                            } else {
                                PaddingValues(0.dp)
                            }
                        ) {
                            groupedAppearances.forEach { (date, appearances) ->
                                item {
                                    val dayLabel = when (date) {
                                        today -> "Today"
                                        today.plus(1, DateTimeUnit.DayBased(1)) -> "Tomorrow"
                                        else -> date.format(DateTime.dayOfWeekFormatLong)
                                    }
                                    Text(
                                        text = dayLabel,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 16.dp
                                        )
                                    )
                                }

                                itemsIndexed(appearances) { index, (name, appearance) ->
                                    UpcomingFavoriteItem(
                                        name = name,
                                        appearance = appearance,
                                        onClick = {
                                            Logger.LogInfo(LOG_TAG, "Navigating to $name at ${appearance.locationName}")
                                            homeViewModel.navigateToMenu(
                                                diningCourt = appearance.locationName,
                                                diningCourtId = DiningCourtIdMap.diningCourtIdMap[appearance.locationName],
                                                mealName = appearance.mealName,
                                                date = appearance.date,
                                                item = name
                                            )
                                        }
                                    )

                                    if (index < appearances.lastIndex) {
                                        HorizontalDivider()
                                    } else if (date != groupedAppearances.keys.last()) {
                                        HorizontalDivider(thickness = 6.dp)
                                    }
                                }
                            }
                        }
                    }

                    if (weekAppearances.isNotEmpty() && todayAppearances.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Button(
                                onClick = {
                                    showMore = !showMore
                                    Logger.LogDebug(LOG_TAG, "Showing more appearances: $showMore")
                                }
                            ) {
                                Text(if (showMore) "Show Less" else "Show More")
                            }
                        }
                    }
                }
            }
        },
        onFailure = {
            Logger.LogError(LOG_TAG, "Error loading upcoming favorites: ${it.message}")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${it.message}")
            }
        }
    )
}

@Composable
fun UpcomingFavoriteItem(
    name: String,
    appearance: Appearance,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = appearance.locationName)
            }

            Text(
                text = "${appearance.mealName} at ${DateTime.parseTime(appearance.date, DateTime.systemTimeZone).format(DateTime.shortTimeFormat)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
