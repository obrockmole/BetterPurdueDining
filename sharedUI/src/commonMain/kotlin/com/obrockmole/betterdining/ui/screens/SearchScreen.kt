package com.obrockmole.betterdining.ui.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import betterpurduedining.shared.generated.resources.*
import com.obrockmole.betterdining.ItemSearchQuery
import com.obrockmole.betterdining.repository.SearchRepository
import com.obrockmole.betterdining.utils.BackHandler
import com.obrockmole.betterdining.utils.DateTime
import com.obrockmole.betterdining.utils.DiningCourtIdMap
import com.obrockmole.betterdining.utils.Logger
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.SearchViewModel
import com.obrockmole.betterdining.viewmodel.SearchViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.datetime.format
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "SearchScreen"

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    homeViewModel: HomeViewModel
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(
            SearchRepository()
        )
    )

    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<ItemSearchQuery.ItemSearch>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var noResults by remember { mutableStateOf(false) }
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onNavigateBack()
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
                        onNavigateBack()
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
                            onNavigateBack = onNavigateBack,
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
    modifier: Modifier = Modifier,
    results: List<ItemSearchQuery.ItemSearch>,
    expandedItemId: String?,
    onNavigateBack: () -> Unit,
    onItemClick: (String) -> Unit,
    homeViewModel: HomeViewModel
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        results.forEach { groupedResult ->
            item {
                ExpandableSearchResultItem(
                    groupedResult = groupedResult,
                    isExpanded = expandedItemId == groupedResult.itemId,
                    onHeaderClick = { onItemClick(groupedResult.itemId) },
                    onAppearanceClick = { appearance ->
                        Logger.LogInfo(LOG_TAG, "Navigating to search result: ${groupedResult.name} at ${appearance.locationName} on ${appearance.date}")
                        homeViewModel.navigateToMenu(
                            diningCourt = appearance.locationName,
                            diningCourtId = DiningCourtIdMap.diningCourtIdMap[appearance.locationName],
                            mealName = appearance.mealName,
                            date = appearance.date,
                            item = groupedResult.name
                        )
                        onNavigateBack()
                    }
                )
            }
        }
    }
}

@Composable
fun ExpandableSearchResultItem(
    modifier: Modifier = Modifier,
    groupedResult: ItemSearchQuery.ItemSearch,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    onAppearanceClick: (ItemSearchQuery.Appearance) -> Unit
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
                text = groupedResult.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${groupedResult.appearances.size} locations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()

        if (isExpanded) {
            groupedResult.appearances.forEach { appearance ->
                AppearanceListItem(
                    appearance = appearance,
                    onAppearanceClick = onAppearanceClick
                )
            }

            if (groupedResult.appearances.isEmpty()) {
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
    modifier: Modifier = Modifier,
    appearance: ItemSearchQuery.Appearance,
    onAppearanceClick: (ItemSearchQuery.Appearance) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onAppearanceClick(appearance) })
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