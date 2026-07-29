package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.GetItemDetailsQuery
import com.obrockmole.kmpbetterdining.utils.DateTime
import com.obrockmole.kmpbetterdining.utils.DiningCourtIdMap
import com.obrockmole.kmpbetterdining.utils.Logger
import com.obrockmole.kmpbetterdining.viewmodel.HomeViewModel
import com.obrockmole.kmpbetterdining.viewmodel.ItemUiState
import com.obrockmole.kmpbetterdining.viewmodel.ItemViewModel
import kmpbetterdining.shared.generated.resources.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.format
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "ItemDetailScreen"

val itemDetails = listOf("Nutrition", "Traits", "Components", "Schedule")

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItemDetailScreen(
    itemName: String,
    itemId: String,
    onNavigateBack: () -> Unit,
    homeViewModel: HomeViewModel,
    itemViewModel: ItemViewModel,
    modifier: Modifier = Modifier
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded for $itemName ($itemId)")

    LaunchedEffect(itemId) {
        itemViewModel.getItem(itemId)
    }

    val uiState = itemViewModel.itemUiState
    var selectedDetailIndex by rememberSaveable { mutableIntStateOf(0) }
    var moreMenuShown by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog && uiState is ItemUiState.Success) {
        Logger.LogDebug(LOG_TAG, "Showing rename dialog")
        RenameItemDialog(
            onDismiss = {
                Logger.LogDebug(LOG_TAG, "Rename dialog dismissed")
                showRenameDialog = false
            },
            onRename = { newName ->
                Logger.LogInfo(LOG_TAG, "Renaming item '${uiState.item.name}' to '$newName'")
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
                actions = {
                    if (uiState is ItemUiState.Success) {
                        IconButton(onClick = {
                            Logger.LogDebug(LOG_TAG, "More menu shown")
                            moreMenuShown = true
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.more_vertical),
                                contentDescription = "More"
                            )
                        }

                        DropdownMenuPopup(
                            expanded = moreMenuShown,
                            onDismissRequest = {
                                Logger.LogDebug(LOG_TAG, "More menu hidden")
                                moreMenuShown = false
                            }
                        ) {
                            DropdownMenuGroup(
                                shapes = MenuDefaults.groupShape(0, 1)
                            ) {
                                val isFavorite = itemViewModel.isFavorite
                                DropdownMenuItem(
                                    text = { Text("Favorite") },
                                    trailingIcon = {
                                        Icon(
                                            painter = if (isFavorite) painterResource(Res.drawable.favorite_filled)
                                            else painterResource(Res.drawable.favorite),
                                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
                                        )
                                    },
                                    onClick = {
                                        Logger.LogDebug(LOG_TAG, "Favorite option clicked ($isFavorite)")
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
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                DropdownMenuItem(
                                    text = { Text("Rename") },
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(Res.drawable.edit),
                                            contentDescription = "Rename item."
                                        )
                                    },
                                    onClick = {
                                        Logger.LogDebug(LOG_TAG, "Rename option clicked")
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
                    Logger.LogDebug(LOG_TAG, "UI loading")
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ItemUiState.Error -> {
                    Logger.LogError(LOG_TAG, "Failed to load UI: ${uiState.message}")
                    Text(
                        text = "Error loading item details.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                is ItemUiState.Success -> {
                    Logger.LogDebug(LOG_TAG, "UI loaded successfully")
                    val item = uiState.item

                    Column {
                        val visibleTabs = remember(item.components) {
                            itemDetails.filter { detail ->
                                when (detail) {
                                    "Nutrition" -> item.isNutritionReady || item.nutritionFacts != null
                                    "Traits" -> !item.traits.isNullOrEmpty()
                                    "Components" -> !item.components.isNullOrEmpty()
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
                                "Nutrition" -> {
                                    Logger.LogDebug(LOG_TAG, "Displaying NutritionDetails")
                                    NutritionDetails(item)
                                }
                                "Traits" -> {
                                    Logger.LogDebug(LOG_TAG, "Displaying TraitsDetails")
                                    TraitsDetails(item)
                                }
                                "Components" -> {
                                    Logger.LogDebug(LOG_TAG, "Displaying ComponentsDetails")
                                    ComponentsDetails(item)
                                }
                                "Schedule" -> {
                                    Logger.LogDebug(LOG_TAG, "Displaying ScheduleDetails")
                                    ScheduleDetails(
                                        item,
                                        homeViewModel,
                                        onNavigateBack = onNavigateBack
                                    )
                                }
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
    onNavigateBack: () -> Unit
) {
    if (item.appearances.isEmpty()) {
        Text(
            "This item is not upcoming soon",
            modifier = Modifier.padding(16.dp)
        )

    } else {
        val today = DateTime.getDate()
        item.appearances

        val groupedAppearances = item.appearances
            .sortedBy { appearance ->
                DateTime.parseDate(appearance.date)
            }
            .groupBy { appearance ->
                DateTime.parseDate(appearance.date)
            }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
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

                itemsIndexed(appearances) { index, appearance ->
                    AppearanceItem(
                        appearance = appearance,
                        onClick = {
                            Logger.LogInfo(LOG_TAG, "Navigating to ${appearance.mealName} menu for ${appearance.locationName} on ${appearance.date} for item ${item.name}")
                            homeViewModel.navigateToMenu(
                                diningCourt = appearance.locationName,
                                diningCourtId = DiningCourtIdMap.diningCourtIdMap[appearance.locationName],
                                mealName = appearance.mealName,
                                date = appearance.date,
                                item = item.name
                            )
                            onNavigateBack()
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
                    text = DateTime.parseTime(appearance.date, DateTime.systemTimeZone).format(DateTime.longTimeFormat),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}