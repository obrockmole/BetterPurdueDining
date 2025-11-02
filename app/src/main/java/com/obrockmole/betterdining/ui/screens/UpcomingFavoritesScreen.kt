package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.models.Appearance
import com.obrockmole.betterdining.repository.UpcomingFavoritesRepository
import com.obrockmole.betterdining.viewmodel.UpcomingFavoritesViewModel
import com.obrockmole.betterdining.viewmodel.UpcomingFavoritesViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun UpcomingFavoritesScreen(modifier: Modifier = Modifier) {
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

            val displayedAppearances = if (showMore) todayAppearances + weekAppearances else todayAppearances

            Column(modifier = modifier.fillMaxSize()) {
                if (displayedAppearances.isEmpty()) {
                    val message = if (showMore) "No upcoming favorites this next week." else "No favorites available."
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = message, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(displayedAppearances) { (name, appearance) ->
                            UpcomingFavoriteItem(name, appearance)
                        }
                    }
                }

                if (weekAppearances.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.BottomStart) {
                        Button(
                            onClick = { showMore = !showMore }
                        ) {
                            Text(if (showMore) "Show Less" else "Show More")
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
fun UpcomingFavoriteItem(name: String, appearance: Appearance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(text = "When: ${appearance.mealName} at ${LocalDateTime.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).format(DateTimeFormatter.ofPattern("h:mm a"))}")
            Text(text = "Location: ${appearance.locationName}")
        }
    }
}
