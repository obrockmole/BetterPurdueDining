package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.models.Appearance
import com.obrockmole.betterdining.repository.UpcomingFavoritesRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.UpcomingFavoritesViewModel
import com.obrockmole.betterdining.viewmodel.UpcomingFavoritesViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun UpcomingFavoritesScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val upcomingFavoritesViewModel: UpcomingFavoritesViewModel = viewModel(
        factory = UpcomingFavoritesViewModelFactory(
            UpcomingFavoritesRepository(AppDatabase.getDatabase(context).favoriteItemDao())
        )
    )

    val upcomingFavoritesResult by upcomingFavoritesViewModel.upcomingFavorites.collectAsState()
    var showMore by remember { mutableStateOf(false) }

    upcomingFavoritesResult.fold(
        onSuccess = { upcomingFavorites ->
            if (upcomingFavorites.all { it.appearances.isEmpty() }) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No upcoming favorites this next week.")
                }
                return@fold
            }

            val today = LocalDate.now()

            val allAppearances = upcomingFavorites.flatMap { favorite ->
                favorite.appearances.map { appearance -> favorite.name to appearance }
            }.distinctBy { (name, appearance) ->
                Triple(name, appearance.mealName, appearance.locationName)
            }.sortedBy { (_, appearance) ->
                LocalDate.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            }

            val todayAppearances = allAppearances.filter { (_, appearance) ->
                LocalDate.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME) == today
            }

            val weekAppearances = allAppearances.filter { (_, appearance) ->
                val date = LocalDate.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                date.isAfter(today) && date.isBefore(today.plusDays(7))
            }

            val groupedAppearances = if (showMore) {
                (todayAppearances + weekAppearances).groupBy { (_, appearance) ->
                    LocalDate.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                }
            } else {
                todayAppearances.groupBy { (_, appearance) ->
                    LocalDate.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                }
            }

            if (groupedAppearances.isEmpty()) {
                val message =
                    if (showMore) "No upcoming favorites found for the next week." else "Nothing available today."
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
                                        today.plusDays(1) -> "Tomorrow"
                                        else -> date.format(DateTimeFormatter.ofPattern("EEEE"))
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
                                            homeViewModel.navigateToMenu(
                                                diningCourt = appearance.locationName,
                                                mealName = appearance.mealName,
                                                date = appearance.date
                                            )
                                        }
                                    )

                                    if (index < appearances.lastIndex) {
                                        HorizontalDivider()
                                    } else if (date != groupedAppearances.keys.last()) {
                                        HorizontalDivider(thickness = 6.dp);
                                    }
                                }
                            }
                        }
                    }

                    if (weekAppearances.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Button(
                                onClick = { showMore = !showMore }
                            ) {
                                Text(if (showMore) "Show Less" else "Show More")
                            }
                        }
                    }
                }
            }
        },
        onFailure = {
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
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = appearance.locationName)
            }

            Text(
                text = "${appearance.mealName} at ${LocalDateTime.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        .format(DateTimeFormatter.ofPattern("HH:mm"))}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun UpcomingFavoriteItemPreview() {
    BetterPurdueDiningTheme() {
        UpcomingFavoriteItem(
            name = "Carne Asada",
            appearance = Appearance(
                date = "2024-06-15T11:00:00Z",
                mealName = "Lunch",
                locationName = "Wiley",
                stationName = "La Fonde"
            ),
            onClick = {}
        )
    }
}