package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToItem: (String, String) -> Unit,
    viewModel: HomeViewModel
) {
    val selectedDiningCourtFromFav by viewModel.selectedDiningCourt.collectAsState()
    val selectedMealNameFromFav by viewModel.selectedMealName.collectAsState()
    val selectedDateFromFav by viewModel.selectedDate.collectAsState()

    var selectedDiningCourt by rememberSaveable { mutableStateOf<String?>(null) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedDiningCourtFromFav) {
        if (selectedDiningCourtFromFav != null) {
            selectedDiningCourt = selectedDiningCourtFromFav
        }
    }

    if (isSearchActive) {
        SearchScreen(
            onBack = { isSearchActive = false },
            homeViewModel = viewModel
        )
    } else if (selectedDiningCourt != null) {
        val menuViewModel: MenuViewModel = viewModel(
            factory = MenuViewModelFactory(MenuRepository())
        )
        DiningCourtDetail(
            diningCourtName = selectedDiningCourt!!,
            viewModel = menuViewModel,
            onBack = {
                selectedDiningCourt = null
                viewModel.clearNavigation()
            },
            onNavigateToItem = onNavigateToItem,
            initialMealName = selectedMealNameFromFav,
            initialDate = selectedDateFromFav
        )

    } else {
        DiningCourtList(
            diningCourts = diningCourtOptions,
            onDiningCourtClicked = { selectedDiningCourt = it },
            onSearchClicked = { isSearchActive = true },
            modifier = modifier
        )
    }
}

@Composable
fun DiningCourtList(
    diningCourts: List<String>,
    onDiningCourtClicked: (String) -> Unit,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
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
                        .clickable(onClick = onSearchClicked)
                        .padding(16.dp),
                    contentDescription = "Search for item."
                )
            }
        }
        items(diningCourts) { diningCourt ->
            DiningCourtListItem(
                diningCourtName = diningCourt,
                onClicked = { onDiningCourtClicked(diningCourt) }
            )
        }
    }
}

@Composable
fun DiningCourtListItem(
    diningCourtName: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClicked)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = diningCourtName
            )
            Icon(
                painter = painterResource(R.drawable.keyboard_arrow_right),
                contentDescription = "See menu."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BetterPurdueDiningTheme {
        HomeScreen(onNavigateToItem = { _, _ -> }, viewModel = HomeViewModel())
    }
}
