package com.obrockmole.betterdining.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemShapes
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.GetItemDetailsQuery
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.repository.RenamedCourtsRepository
import com.obrockmole.betterdining.repository.RenamedItemsRepository
import com.obrockmole.betterdining.repository.StartLocationsRepository
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.ItemUiState
import com.obrockmole.betterdining.viewmodel.ItemViewModel
import com.obrockmole.betterdining.viewmodel.ItemViewModelFactory
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// "Components" wont fit :(
val itemDetails = listOf("Nutrition", "Traits", "Component", "Schedule")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItemDetailScreen(
    itemName: String,
    itemId: String,
    onNavigateBack: () -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val itemViewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(
            MenuRepository(),
            FavoritesRepository(AppDatabase.getDatabase(context).favoriteItemDao()),
            RenamedItemsRepository(AppDatabase.getDatabase(context).renamedItemDao())
        )
    )

    LaunchedEffect(itemId) {
        itemViewModel.getItem(itemId)
    }

    val uiState = itemViewModel.itemUiState
    var selectedDetailIndex by rememberSaveable { mutableIntStateOf(0) }
    var moreMenuShown by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog && uiState is ItemUiState.Success) {
        RenameItemDialog(
            onDismiss = { showRenameDialog = false },
            onRename = { newName ->
                itemViewModel.renameItem(uiState.item.itemId, newName)
                showRenameDialog = false
            },
            currentName = if (itemViewModel.isRenamed) itemViewModel.renamedName else uiState.item.name,
            officialName = uiState.item.name
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (itemViewModel.isRenamed) {
                        Text(text = itemViewModel.renamedName)
                    } else {
                        Text(text = itemName)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState is ItemUiState.Success) {
                        IconButton(onClick = { moreMenuShown = true }) {
                            Icon(
                                painter = painterResource(R.drawable.more_vertical),
                                contentDescription = "More"
                            )
                        }

                        DropdownMenuPopup(
                            expanded = moreMenuShown,
                            onDismissRequest = { moreMenuShown = false }
                        ) {
                            DropdownMenuGroup(
                                shapes = MenuDefaults.groupShape(0, 1)
                            ) {
                                val isFavorite = itemViewModel.isFavorite
                                DropdownMenuItem(
                                    text = { Text("Favorite") },
                                    trailingIcon = {
                                        Icon(
                                            painter = if (isFavorite) painterResource(R.drawable.favorite_filled)
                                            else painterResource(R.drawable.favorite),
                                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
                                        )
                                    },
                                    onClick = {
                                        itemViewModel.toggleFavorite(uiState.item)
                                        moreMenuShown = false
                                    },
                                    selected = false,
                                    shapes = MenuItemShapes(
                                        MenuDefaults.leadingItemShape,
                                        MenuDefaults.selectedItemShape
                                    )
                                )

                                HorizontalDivider(
                                    modifier =
                                        Modifier.padding(horizontal = MenuDefaults.HorizontalDividerPadding)
                                )

                                DropdownMenuItem(
                                    text = { Text("Rename") },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.edit),
                                            contentDescription = "Rename item."
                                        )
                                    },
                                    onClick = {
                                        moreMenuShown = false
                                        showRenameDialog = true
                                    },
                                    selected = false,
                                    shapes = MenuItemShapes(
                                        MenuDefaults.trailingItemShape,
                                        MenuDefaults.selectedItemShape
                                    )
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is ItemUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ItemUiState.Error -> {
                    Text("Error loading item details", modifier = Modifier.align(Alignment.Center))
                }

                is ItemUiState.Success -> {
                    val item = uiState.item

                    Column {
                        val visibleTabs = remember(item.components) {
                            itemDetails.filter { detail ->
                                when (detail) {
                                    "Nutrition" -> item.isNutritionReady || item.nutritionFacts != null
                                    "Traits" -> !item.traits.isNullOrEmpty()
                                    "Component" -> !item.components.isNullOrEmpty()
                                    else -> true
                                }
                            }
                        }

                        if (visibleTabs.isNotEmpty()) {
                            SecondaryTabRow(
                                selectedTabIndex = selectedDetailIndex.coerceIn(
                                    0,
                                    visibleTabs.lastIndex
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                visibleTabs.forEachIndexed { index, detail ->
                                    Tab(
                                        selected = selectedDetailIndex == index,
                                        onClick = { selectedDetailIndex = index },
                                        text = { Text(detail) }
                                    )
                                }
                            }

                            when (visibleTabs.getOrNull(selectedDetailIndex)) {
                                "Nutrition" -> NutritionDetails(item)
                                "Traits" -> TraitsDetails(item)
                                "Component" -> ComponentsDetails(item)
                                "Schedule" -> ScheduleDetails(
                                    item,
                                    homeViewModel,
                                    onBack = onNavigateBack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenameItemDialog(
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    currentName: String,
    officialName: String
) {
    var text by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Item") },
        text = {
            TextField(
                value = text,
                placeholder = { Text(officialName) },
                onValueChange = { text = it },
                label = { Text("New Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onRename(text.ifEmpty { officialName }) }
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NutritionDetails(
    item: GetItemDetailsQuery.ItemByItemId
) {
    if (!item.isNutritionReady || item.nutritionFacts == null) {
        Text(
            "No nutrition details available.",
            modifier = Modifier.padding(16.dp)
        )

    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item.nutritionFacts.forEach { fact ->
                item {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = fact.name)

                                fact.label?.let { Text(text = it) }
                                if (fact.label == null) {
                                    fact.dailyValueLabel?.let { Text(text = "$it Daily Value") }
                                }
                            }
                        }

                        HorizontalDivider()
                    }
                }
            }

            item {
                item.ingredients?.let {
                    Text(
                        "Ingredients: $it",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TraitsDetails(
    item: GetItemDetailsQuery.ItemByItemId
) {
    if (item.traits.isNullOrEmpty()) {
        Text(
            "This item has no traits",
            modifier = Modifier.padding(16.dp)
        )

    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item.traits.forEach { trait ->
                item {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = trait.name)
                                Text(text = trait.type)
                            }
                        }

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun ComponentsDetails(
    item: GetItemDetailsQuery.ItemByItemId
) {
    if (item.components.isNullOrEmpty()) {
        Text(
            "This item has no components",
            modifier = Modifier.padding(16.dp)
        )

    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item.components.forEach { component ->
                item {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = component.name)
                            }
                        }

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleDetails(
    item: GetItemDetailsQuery.ItemByItemId,
    homeViewModel: HomeViewModel,
    onBack: () -> Unit
) {
    if (item.appearances.isEmpty()) {
        Text(
            "This item is not upcoming soon",
            modifier = Modifier.padding(16.dp)
        )

    } else {
        val today = LocalDate.now()
        item.appearances

        val groupedAppearances = item.appearances
            .sortedBy { appearance ->
                LocalDate.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            }
            .groupBy { appearance ->
                LocalDate.parse(appearance.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
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

                itemsIndexed(appearances) { index, appearance ->
                    AppearanceItem(
                        appearance = appearance,
                        onClick = {
                            Log.e(
                                "ItemDetailScreen",
                                "Navigating to ${appearance.locationName} at ${appearance.mealName}"
                            )
                            homeViewModel.navigateToMenu(
                                diningCourt = appearance.locationName,
                                diningCourtId = null,
                                mealName = appearance.mealName,
                                date = appearance.date,
                                item = item.name
                            )
                            onBack()
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
}

@Composable
fun AppearanceItem(
    appearance: GetItemDetailsQuery.Appearance,
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
                Text(text = appearance.locationName, style = MaterialTheme.typography.titleMedium)
                Text(text = appearance.mealName)
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = appearance.stationName)
                Text(
                    text = "${
                        OffsetDateTime.parse(
                            appearance.date,
                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        )
                            .atZoneSameInstant(ZoneId.systemDefault()).toLocalTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm"))
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailPreview() {
    val context = LocalContext.current
    ItemDetailScreen(
        itemName = "Carne Asada",
        itemId = "12345-67890",
        onNavigateBack = {},
        homeViewModel = HomeViewModel(
            StartLocationsRepository(),
            RenamedCourtsRepository(AppDatabase.getDatabase(context).renamedDiningCourtDao())
        )
    )
}
