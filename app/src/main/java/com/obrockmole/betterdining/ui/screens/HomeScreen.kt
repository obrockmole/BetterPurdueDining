package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.HomeViewModel
import com.obrockmole.betterdining.viewmodel.MenuViewModel
import com.obrockmole.betterdining.viewmodel.MenuViewModelFactory

val diningCourtOptions = listOf("Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor")
val quickBiteOptions = listOf("1bowl", "Pete's Za", "Sushi Boss")

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String, String) -> Unit,
    viewModel: HomeViewModel
) {
    val selectedDiningCourtFromFav by viewModel.selectedDiningCourt.collectAsState()
    val selectedMealNameFromFav by viewModel.selectedMealName.collectAsState()
    val selectedDateFromFav by viewModel.selectedDate.collectAsState()

    var selectedFoodLocation by rememberSaveable { mutableStateOf<String?>(null) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedDiningCourtFromFav) {
        if (selectedDiningCourtFromFav != null) {
            selectedFoodLocation = selectedDiningCourtFromFav
        }
    }

    if (isSearchActive) {
        SearchScreen(
            onBack = { isSearchActive = false },
            homeViewModel = viewModel
        )
    } else if (selectedFoodLocation != null) {
        val menuViewModel: MenuViewModel = viewModel(
            factory = MenuViewModelFactory(MenuRepository())
        )
        FoodLocationDetail(
            name = when (selectedFoodLocation) {
                "1bowl at Meredith Hall" -> "1bowl"
                "Pete's Za at Tarkington Hall" -> "Pete's Za"
                "Sushi Boss at Meredith Hall" -> "Sushi Boss"
                else -> selectedFoodLocation!!
            },
            nameFormal = selectedFoodLocation!!,
            viewModel = menuViewModel,
            onBack = {
                selectedFoodLocation = null
                viewModel.clearNavigation()
            },
            onNavigateToItem = onNavigateToItem,
            initialMealName = selectedMealNameFromFav,
            initialDate = selectedDateFromFav
        )

    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dining Courts",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        modifier = Modifier
                            .clickable(onClick = { isSearchActive = true })
                            .padding(16.dp),
                        contentDescription = "Search for item."
                    )
                }
            }
            items(diningCourtOptions) { diningCourt ->
                DiningCourtListItem(
                    diningCourtName = diningCourt,
                    onClicked = { selectedFoodLocation = diningCourt }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quick Bites",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            items(quickBiteOptions) { quickBite ->
                QuickBiteListItem(
                    quickBiteName = quickBite,
                    onClicked = {
                        selectedFoodLocation = when (quickBite) {
                            "1bowl" -> "1bowl at Meredith Hall"
                            "Pete's Za" -> "Pete's Za at Tarkington Hall"
                            "Sushi Boss" -> "Sushi Boss at Meredith Hall"
                            else -> quickBite
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DiningCourtListItem(
    diningCourtName: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diningCourtIcon = when (diningCourtName.lowercase()) {
        "earhart" -> R.drawable.earhart_icon
        "ford" -> R.drawable.ford_icon
        "hillenbrand" -> R.drawable.hillenbrand_icon
        "wiley" -> R.drawable.wiley_icon
        "windsor" -> R.drawable.windsor_icon
        else -> R.drawable.app_icon
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClicked)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = diningCourtName,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Icon(
            painter = painterResource(R.drawable.keyboard_arrow_right),
            contentDescription = "See menu."
        )
    }
    HorizontalDivider()
}


@Composable
fun QuickBiteListItem(
    quickBiteName: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickBiteIcon = when (quickBiteName.lowercase()) {
        "1bowl" -> R.drawable.onebowl_icon
        "pete's za" -> R.drawable.petes_icon
        "sushi boss" -> R.drawable.sushiboss_icon
        else -> R.drawable.app_icon
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClicked)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quickBiteName,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Icon(
            painter = painterResource(R.drawable.keyboard_arrow_right),
            contentDescription = "See menu."
        )
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BetterPurdueDiningTheme {
        HomeScreen(
            onNavigateToItem = { _, _ -> },
            viewModel = HomeViewModel()
        )
    }
}
