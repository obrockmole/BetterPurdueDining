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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLocationDetail(
    name: String,
    nameFormal: String,
    viewModel: MenuViewModel,
    onBack: () -> Unit,
    onNavigateToItem: (String, String) -> Unit,
    initialMealName: String?,
    initialDate: String?,
    initialItemName: String?
) {
    BackHandler {
        onBack()
    }

    var displayedDate by remember {
        mutableStateOf(
            initialDate?.let {
                LocalDate.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } ?: LocalDate.now()
        )
    }

    LaunchedEffect(nameFormal, displayedDate) {
        viewModel.getMenu(nameFormal, displayedDate.toString())
    }

    var selectedMealIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                when (val uiState = viewModel.menuUiState) {
                    is MenuUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    is MenuUiState.Error -> {
                        AlertDialog(
                            onDismissRequest = onBack,
                            title = { Text(text = "Oopsie Poopsie") },
                            text = { Text(text = "Purdue did a fucky wucky.") },
                            confirmButton = {
                                TextButton(onClick = onBack) {
                                    Text("God Damnit.")
                                }
                            }
                        )
                    }

                    is MenuUiState.Success -> {
                        val menuData = uiState.data
                        val meals = menuData?.meals

                        LaunchedEffect(meals, initialMealName) {
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
                                val currentHour = LocalDateTime.now(ZoneId.of("America/New_York"))
                                    .toLocalTime().hour
                                meals.forEachIndexed { index, meal ->
                                    if (!meal.stations.isEmpty()) {
                                        val startTime = LocalDateTime.parse(
                                            meal.startTime,
                                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                        ).toLocalTime().hour
                                        val endTime = LocalDateTime.parse(
                                            meal.endTime,
                                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                        ).toLocalTime().hour

                                        if (currentHour in startTime until endTime) {
                                            selectedMealIndex = index
                                            return@forEachIndexed
                                        } else if (currentHour < startTime) {
                                            selectedMealIndex = index
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
                                    onClick = { displayedDate = displayedDate.minusDays(1) },
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
                                    onClick = { displayedDate = displayedDate.plusDays(1) },
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
                                        onClick = { selectedMealIndex = index },
                                        text = { Text(meal.name) }
                                    )
                                }
                            }

                            if (meals?.isNotEmpty() == true && selectedMealIndex < meals.size) {
                                if (meals[selectedMealIndex].stations.isEmpty()) {
                                    Text(
                                        "No meal is being served.",
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    )
                                } else {
                                    MealDetail(
                                        meal = meals[selectedMealIndex],
                                        onNavigateToItem = onNavigateToItem,
                                        initialItemName = initialItemName
                                    )
                                }
                            } else {
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
fun MealDetail(
    meal: MealDisplay,
    onNavigateToItem: (String, String) -> Unit,
    initialItemName: String? = null
) {
    val listState = rememberLazyListState()

    LaunchedEffect(initialItemName) {
        if (initialItemName != null) {
            var targetIndex = 0
            var found = false

            for (station in meal.stations) {
                targetIndex++
                for (item in station.items) {
                    if (item.originalItem.item.name == initialItemName) {
                        found = true
                        break
                    }
                    targetIndex++
                }
                if (found) break
            }

            if (found) {
                delay(100)
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
                    isHighlighted = initialItemName != null && itemWrapper.originalItem.item.name == initialItemName,
                    onNavigateToItem = onNavigateToItem,
                    showDivider = index < station.items.size - 1
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
    showDivider: Boolean
) {
    val backgroundColor = remember { Animatable(Color.Transparent) }
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    var animationPlayed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        //FIXME: Highlight animation keeps playing everytime the page is refreshed
        if (isHighlighted && !animationPlayed) {
            animationPlayed = true
            delay(150)

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
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
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

//                if (itemWrapper.originalItem.specialName != null) {
//                    Text(text = itemWrapper.originalItem.specialName)
//                } else {
//                    Text(text = itemWrapper.displayName)
//                }
                Text(text = itemWrapper.displayName)
            }
        }

        if (showDivider) {
            HorizontalDivider()
        }
    }
}
