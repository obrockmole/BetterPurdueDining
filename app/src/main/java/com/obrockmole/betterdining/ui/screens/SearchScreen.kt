package com.obrockmole.betterdining.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.ItemSearchQuery
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.repository.SearchRepository
import com.obrockmole.betterdining.repository.StartLocationsRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.SearchViewModel
import com.obrockmole.betterdining.viewmodel.SearchViewModelFactory
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(SearchRepository())
    )

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember {
        mutableStateOf<List<ItemSearchQuery.ItemSearch>>(
            emptyList()
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    BackHandler {
        onBack()
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isLoading = true
            hasSearched = false
            delay(450)

            searchResults = searchViewModel.searchItems(searchQuery)

            isLoading = false
            hasSearched = true

        } else {
            searchResults = emptyList()
            isLoading = false
            hasSearched = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Search") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search for items...") },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                painter = painterResource(R.drawable.close),
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
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    hasSearched && searchResults.isEmpty() -> {
                        Text(
                            text = "No items found for \"$searchQuery\"",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    searchResults.isNotEmpty() -> {
                        SearchResultsList(
                            results = searchResults,
                            homeViewModel = homeViewModel,
                            onBack = onBack,
                            expandedItemId = expandedItemId,
                            onItemClick = { itemId ->
                                expandedItemId = if (expandedItemId == itemId) null else itemId
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
    results: List<ItemSearchQuery.ItemSearch>,
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
                    isExpanded = expandedItemId == groupedResult.itemId,
                    onHeaderClick = { onItemClick(groupedResult.itemId) },
                    onAppearanceClick = { appearance ->
                        homeViewModel.navigateToMenu(
                            diningCourt = appearance.locationName,
                            mealName = appearance.mealName,
                            date = appearance.date
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
    groupedResult: ItemSearchQuery.ItemSearch,
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
                    painter = painterResource(R.drawable.keyboard_arrow_down),
                    contentDescription = "Collapse",
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.keyboard_arrow_right),
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
                    onClick = { onAppearanceClick(appearance) }
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

            val dateTime = LocalDateTime.parse(
                appearance.date,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            )
            val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
            val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))

            Text(
                text = "$formattedDate at $formattedTime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    BetterPurdueDiningTheme {
        SearchScreen(
            onBack = {},
            homeViewModel = HomeViewModel(
                StartLocationsRepository()
            )
        )
    }
}
