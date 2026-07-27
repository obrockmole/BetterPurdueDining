package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.ItemSearchQuery
import com.obrockmole.kmpbetterdining.utils.BackHandler
import com.obrockmole.kmpbetterdining.utils.DateTime
import com.obrockmole.kmpbetterdining.utils.Logger
import com.obrockmole.kmpbetterdining.viewmodel.HomeViewModel
import com.obrockmole.kmpbetterdining.viewmodel.SearchItemDisplay
import com.obrockmole.kmpbetterdining.viewmodel.SearchViewModel
import kmpbetterdining.shared.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.datetime.format
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "SearchScreen"

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchItemDisplay>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var noResults by remember { mutableStateOf(false) }
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onBack()
    }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            Logger.LogDebug(LOG_TAG, "Search query changed to '$query'")
            isLoading = true
            noResults = false
            delay(450)

            searchResults = searchViewModel.searchItems(query)
            Logger.LogDebug(LOG_TAG, "Search returned ${searchResults.size} results")

            isLoading = false
            noResults = searchResults.isEmpty()

        } else {
            Logger.LogDebug(LOG_TAG, "Search query cleared")
            searchResults = emptyList()
            isLoading = false
            noResults = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = {
                        Logger.LogDebug(LOG_TAG, "Back navigation clicked")
                        onBack()
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search for items...") },
                singleLine = true,
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                painter = painterResource(Res.drawable.close),
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        Logger.LogDebug(LOG_TAG, "UI loading")
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    noResults -> {
                        Logger.LogDebug(LOG_TAG, "UI loaded with no results for query '$query'")
                        Text(
                            text = "No items found for \"$query\"",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    searchResults.isNotEmpty() -> {
                        Logger.LogDebug(LOG_TAG, "UI loaded successfully with ${searchResults.size} results")
                        SearchResultsList(
                            results = searchResults,
                            homeViewModel = homeViewModel,
                            onBack = onBack,
                            expandedItemId = expandedItemId,
                            onItemClick = { itemId ->
                                expandedItemId = if (expandedItemId == itemId) null else itemId
                                Logger.LogDebug(LOG_TAG, "Search result $itemId clicked, expandedItemId = $expandedItemId")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultsList(
    results: List<SearchItemDisplay>,
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    expandedItemId: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        results.forEach { groupedResult ->
            item {
                ExpandableSearchResultItem(
                    groupedResult = groupedResult,
                    isExpanded = expandedItemId == groupedResult.originalItem.itemId,
                    onHeaderClick = { onItemClick(groupedResult.originalItem.itemId) },
                    onAppearanceClick = { appearance ->
                        Logger.LogInfo(LOG_TAG, "Navigating to search result: ${groupedResult.displayName} at ${appearance.locationName} on ${appearance.date}")
                        homeViewModel.navigateToMenu(
                            diningCourt = appearance.locationName,
                            diningCourtId = null,
                            mealName = appearance.mealName,
                            date = appearance.date,
                            item = groupedResult.displayName
                        )
                        onBack()
                    }
                )
            }
        }
    }
}

@Composable
fun ExpandableSearchResultItem(
    groupedResult: SearchItemDisplay,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    onAppearanceClick: (ItemSearchQuery.Appearance) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onHeaderClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isExpanded) {
                Icon(
                    painter = painterResource(Res.drawable.keyboard_arrow_down),
                    contentDescription = "Collapse",
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.keyboard_arrow_right),
                    contentDescription = "Expand",
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Text(
                text = groupedResult.displayName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${groupedResult.originalItem.appearances.size} locations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()

        if (isExpanded) {
            groupedResult.originalItem.appearances.forEach { appearance ->
                AppearanceListItem(
                    appearance = appearance,
                    onClick = { onAppearanceClick(appearance) }
                )
            }

            if (groupedResult.originalItem.appearances.isEmpty()) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nothings coming up biggie",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "5:00 PM, Monday the 4th of never",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun AppearanceListItem(
    appearance: ItemSearchQuery.Appearance,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${appearance.locationName} - ${appearance.mealName}",
                style = MaterialTheme.typography.bodyMedium
            )

            val dateTime = DateTime.parseDateTime(appearance.date, DateTime.systemTimeZone)
            val formattedDate = dateTime.date.format(DateTime.dayOfWeekFormatLong)
            val formattedTime = dateTime.time.format(DateTime.longTimeFormat)

            Text(
                text = "$formattedDate at $formattedTime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider()
}