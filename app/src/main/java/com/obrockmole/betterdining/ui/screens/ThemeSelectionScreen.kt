package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obrockmole.betterdining.R
import com.obrockmole.betterdining.database.AppDatabase
import com.obrockmole.betterdining.repository.FavoritesRepository
import com.obrockmole.betterdining.repository.UserPreferencesRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.SettingsViewModel
import com.obrockmole.betterdining.viewmodel.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            UserPreferencesRepository(context),
            FavoritesRepository(AppDatabase.getDatabase(context).favoriteItemDao())
        )
    )
    val appTheme by settingsViewModel.appTheme.collectAsState()

    val themeOptions = listOf("Material", "Dark", "Light", "Cancer")

    var loading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Select App Theme") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                themeOptions.forEachIndexed { index, theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (appTheme != theme) {
                                    loading = true
                                    onNavigateBack()
                                    settingsViewModel.setAppTheme(theme)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = theme,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        RadioButton(
                            selected = appTheme == theme,
                            onClick = {
                                if (appTheme != theme) {
                                    loading = true
                                    onNavigateBack()
                                    settingsViewModel.setAppTheme(theme)
                                }
                            }
                        )
                    }

                    if (index < themeOptions.size - 1) {
                        HorizontalDivider(
                            color = Color(0xFF2F2F2F),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeSelectionScreenPreview() {
    BetterPurdueDiningTheme {
        ThemeSelectionScreen(onNavigateBack = {})
    }
}
