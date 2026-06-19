package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.utils.Logger

private const val LOG_TAG = "UpcomingFavoritesScreen"

@Composable
fun UpcomingFavoritesScreen(
    modifier: Modifier = Modifier
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val upcomingFavoritesResult = emptyList<String>()
    var showMore by remember { mutableStateOf(false) }

    val groupedAppearances = emptyList<String>()
    if (groupedAppearances.isEmpty()) {
        val message =
            if (showMore) "No upcoming favorites found for the next week." else "Nothing available today."
        Logger.LogDebug(LOG_TAG, message)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = message, modifier = Modifier.padding(16.dp))
        }
    }
}