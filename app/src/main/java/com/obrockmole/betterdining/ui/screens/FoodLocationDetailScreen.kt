package com.obrockmole.betterdining.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.viewmodel.MealDisplay
import com.obrockmole.betterdining.viewmodel.MenuItemDisplay
import com.obrockmole.betterdining.viewmodel.MenuUiState
import com.obrockmole.betterdining.viewmodel.MenuViewModel
import com.obrockmole.betterdining.viewmodel.StationDisplay
import com.obrockmole.betterdining.utils.Logger
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val LOG_TAG = "FoodLocationDetailScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodLocationDetailScreen(
    name: String,
    courtId: String?,
    menuViewModel: MenuViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String, String) -> Unit,
    initialMealName: String?,
    initialDate: String?,
    initialItemName: String?
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded for $name")
    BackHandler {
        onNavigateBack()
    }

    var displayedDate by remember {
        mutableStateOf(
            initialDate?.let {
                LocalDate.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } ?: LocalDate.now(ZoneId.of("America/New_York"))
        )
    }

    LaunchedEffect(name, courtId, displayedDate) {
        Logger.LogDebug(LOG_TAG, "Fetching menu for $displayedDate")
        menuViewModel.getMenu(name, courtId, displayedDate.toString())
    }

    var firstVisit = rememberSaveable { mutableStateOf(true) }

    val uiState = menuViewModel.menuUiState
    var selectedMealIndex by rememberSaveable { mutableIntStateOf(0) }
    var moreMenuShown by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    var itemToHighlight by rememberSaveable { mutableStateOf(initialItemName) }

    if (showRenameDialog && uiState is MenuUiState.Success) {
        Logger.LogDebug(LOG_TAG, "Showing rename dialog")
        RenameDiningCourtDialog(
            onDismiss = {
                Logger.LogDebug(LOG_TAG, "Rename dialog dismissed")
                showRenameDialog = false
            },
            onRename = { newName ->
                Logger.LogInfo(LOG_TAG, "Renaming dining court '$name' to '$newName'")
                menuViewModel.renameDiningCourt(uiState.data!!.courtId, newName)
                showRenameDialog = false
            },
            currentName = if (menuViewModel.isRenamed) menuViewModel.renamedName else name,
            officialName = name
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (menuViewModel.isRenamed) {
                        Text(text = menuViewModel.renamedName)
                    } else {
                        Text(text = name)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        Logger.LogDebug(LOG_TAG, "Back navigation clicked")
                        onNavigateBack()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState is MenuUiState.Success) {
                        IconButton(onClick = {
                            Logger.LogDebug(LOG_TAG, "More menu shown")
                            moreMenuShown = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.more_vertical),
                                contentDescription = "More"
                            )
                        }
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
                            DropdownMenuItem(
                                text = { Text("Rename") },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit),
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
                                    MenuDefaults.standaloneItemShape,
                                    MenuDefaults.selectedItemShape
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is MenuUiState.Loading -> {
                        Logger.LogDebug(LOG_TAG, "UI loading")
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    is MenuUiState.Error -> {
                        Logger.LogError(LOG_TAG, "Failed to load UI: ${uiState.message}")
                        Text(
                            text = "Error loading menu.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }

                    is MenuUiState.Success -> {
                        Logger.LogDebug(LOG_TAG, "UI loaded successfully")
                        val menuData = uiState.data
                        val meals = menuData?.meals

                        LaunchedEffect(meals, initialMealName) {
                            if (firstVisit.value) {
                                firstVisit.value = false

                                meals?.size?.let {
                                    if (selectedMealIndex >= it) {
                                        selectedMealIndex = 0
                                    }
                                }

                                if (initialMealName != null) {
                                    val index = meals!!.indexOfFirst { it.name == initialMealName }
                                    if (index != -1) {
                                        selectedMealIndex = index
                                    }
                                } else {
                                    val currentHour =
                                        LocalDateTime.now(ZoneId.of("America/New_York"))
                                            .toLocalTime().hour
                                    meals?.let { mealList ->
                                        for ((index, meal) in mealList.withIndex()) {
                                            if (meal.startTime == null || meal.endTime == null) continue

                                            val startTime = LocalDateTime.parse(
                                                meal.startTime,
                                                DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                            ).toLocalTime().hour
                                            val endTime = LocalDateTime.parse(
                                                meal.endTime,
                                                DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                            ).toLocalTime().hour

                                            if (currentHour in startTime..<endTime) {
                                                selectedMealIndex = index
                                                break
                                            } else if (currentHour < startTime) {
                                                selectedMealIndex = index
                                                break
                                            }

                                            if (index == mealList.lastIndex) {
                                                selectedMealIndex = 0
                                                displayedDate = displayedDate.plusDays(1)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = {
                                        Logger.LogDebug(LOG_TAG, "Previous day clicked")
                                        displayedDate = displayedDate.minusDays(1)
                                    },
                                    modifier = Modifier.padding(start = 48.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.keyboard_arrow_left),
                                        contentDescription = "Previous day."
                                    )
                                }

                                val today = LocalDate.now()
                                val tomorrow = today.plusDays(1)
                                val yesterday = today.minusDays(1)

                                val dateText = when (displayedDate) {
                                    today -> "Today"
                                    tomorrow -> "Tomorrow"
                                    yesterday -> "Yesterday"
                                    else -> displayedDate.format(DateTimeFormatter.ofPattern("MMM d"))
                                }

                                Text(
                                    text = dateText,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                IconButton(
                                    onClick = {
                                        Logger.LogDebug(LOG_TAG, "Next day clicked")
                                        displayedDate = displayedDate.plusDays(1)
                                    },
                                    modifier = Modifier.padding(end = 48.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.keyboard_arrow_right),
                                        contentDescription = "Next day."
                                    )
                                }
                            }

                            SecondaryTabRow(
                                selectedTabIndex = selectedMealIndex,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                meals?.forEachIndexed { index, meal ->
                                    Tab(
                                        selected = selectedMealIndex == index,
                                        onClick = {
                                            Logger.LogDebug(LOG_TAG, "Meal clicked: ${meal.name}")
                                            selectedMealIndex = index
                                        },
                                        text = { Text(meal.name) }
                                    )
                                }
                            }

                            if (meals?.isNotEmpty() == true && selectedMealIndex < meals.size) {
                                if (meals[selectedMealIndex].stations.isEmpty()) {
                                    Logger.LogInfo(LOG_TAG, "No stations for meal ${meals[selectedMealIndex].name}")
                                    Text(
                                        "No meal is being served.",
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    )
                                } else {
                                    Logger.LogDebug(LOG_TAG, "Displaying meal details for ${meals[selectedMealIndex].name}")
                                    MealDetail(
                                        meal = meals[selectedMealIndex],
                                        onNavigateToItem = { name, id ->
                                            Logger.LogInfo(LOG_TAG, "Navigating to item: $name ($id)")
                                            onNavigateToItem(name, id)
                                        },
                                        itemToHighlight = itemToHighlight,
                                        onItemHighlighted = { itemToHighlight = null }
                                    )
                                }
                            } else {
                                Logger.LogDebug(LOG_TAG, "No meals available for this date")
                                Text(
                                    "No meals are being served.",
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(16.dp)
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
fun RenameDiningCourtDialog(
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    currentName: String,
    officialName: String
) {
    var text by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Dining Court") },
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
                onClick = { onRename(text) }
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
fun MealDetail(
    meal: MealDisplay,
    onNavigateToItem: (String, String) -> Unit,
    itemToHighlight: String? = null,
    onItemHighlighted: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(itemToHighlight, meal) {
        if (itemToHighlight != null) {
            var targetIndex = 0
            var found = false

            for (station in meal.stations) {
                targetIndex++
                for (item in station.items) {
                    if (item.originalItem.item.name == itemToHighlight) {
                        found = true
                        break
                    }
                    targetIndex++
                }
                if (found) break
            }

            if (found) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        meal.stations.forEach { station ->
            item {
                StationHeader(station = station)
            }

            itemsIndexed(station.items) { index, itemWrapper ->
                StationItem(
                    itemWrapper = itemWrapper,
                    isHighlighted = itemToHighlight != null && itemWrapper.originalItem.item.name == itemToHighlight,
                    onNavigateToItem = onNavigateToItem,
                    showDivider = index < station.items.size - 1,
                    onItemHighlighted = onItemHighlighted
                )
            }
        }
    }
}

@Composable
fun StationHeader(station: StationDisplay) {
    Text(
        text = station.name,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun StationItem(
    itemWrapper: MenuItemDisplay,
    isHighlighted: Boolean,
    onNavigateToItem: (String, String) -> Unit,
    showDivider: Boolean,
    onItemHighlighted: () -> Unit = {}
) {
    val backgroundColor = remember { Animatable(Color.Transparent) }
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

    LaunchedEffect(isHighlighted) {
        if (isHighlighted) {
            backgroundColor.animateTo(
                targetValue = highlightColor,
                animationSpec = tween(durationMillis = 200)
            )
            backgroundColor.animateTo(
                targetValue = Color.Transparent,
                animationSpec = tween(durationMillis = 200)
            )
            backgroundColor.animateTo(
                targetValue = highlightColor,
                animationSpec = tween(durationMillis = 200)
            )
            backgroundColor.animateTo(
                targetValue = Color.Transparent,
                animationSpec = tween(durationMillis = 800)
            )

            onItemHighlighted()
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    Logger.LogDebug(LOG_TAG, "Navigating to ${itemWrapper.displayName}")
                    onNavigateToItem(
                        itemWrapper.originalItem.item.name,
                        itemWrapper.originalItem.item.itemId
                    )
                })
                .background(backgroundColor.value)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (itemWrapper.originalItem.hasComponents) {
                    Icon(
                        painter = painterResource(id = R.drawable.stacks),
                        modifier = Modifier.padding(end = 8.dp),
                        contentDescription = ""
                    )
                }

                Text(text = itemWrapper.displayName)
            }
        }

        if (showDivider) {
            HorizontalDivider()
        }
    }
}

