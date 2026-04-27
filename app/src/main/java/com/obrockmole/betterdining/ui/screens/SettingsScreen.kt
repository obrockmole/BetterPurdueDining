package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToDefaultScreen: () -> Unit = {},
    onNavigateToTheme: () -> Unit = {},
    onNavigateToNavStyle: () -> Unit = {},
    onNavigateToLicensesScreen: () -> Unit = {},
    onNavigateToLogLevel: () -> Unit = {}
) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            UserPreferencesRepository(context),
            FavoritesRepository(AppDatabase.getDatabase(context).favoriteItemDao())
        )
    )
    val defaultScreen by settingsViewModel.defaultScreen.collectAsState()
    val appTheme by settingsViewModel.appTheme.collectAsState()
    val navStyle by settingsViewModel.navStyle.collectAsState()
    val logLevel by settingsViewModel.logLevel.collectAsState()

    var showImportDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (navStyle == "Bottom") {
                item {
                    Text(
                        text = "Settings",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.headlineMediumEmphasized
                    )
                }
            }

            item {
                SettingsSectionHeader(title = "Preferences")
            }

            item {
                NavigationalSetting(
                    title = "Default Screen",
                    value = defaultScreen,
                    onClick = onNavigateToDefaultScreen
                )
                HorizontalDivider()
            }

            item {
                NavigationalSetting(
                    title = "Theme",
                    value = appTheme,
                    onClick = onNavigateToTheme
                )
                HorizontalDivider()
            }

            item {
                NavigationalSetting(
                    title = "Navigation Style",
                    value = navStyle,
                    onClick = onNavigateToNavStyle
                )
                HorizontalDivider()
            }

            item {
                NavigationalSetting(
                    title = "Logging",
                    value = logLevel,
                    onClick = onNavigateToLogLevel
                )
            }

            item {
                HorizontalDivider(thickness = 6.dp)
                SettingsSectionHeader(title = "About")
            }

            item {
                NavigationalSetting(
                    title = "Licenses",
                    onClick = onNavigateToLicensesScreen
                )
                HorizontalDivider()
            }

            item {
                InformationSetting(title = "Version", value = "1.0.0")
                HorizontalDivider()
            }

            item {
                ActionSetting(
                    title = "Check For Updates",
                    onClick = {
                        showUpdateDialog = true
                    }
                )
                HorizontalDivider(thickness = 6.dp)
            }

            item {
                ActionSetting(
                    title = "Import Favorites",
                    onClick = {
                        showImportDialog = true
                    }
                )
                HorizontalDivider()
            }
        }

        if (showImportDialog) {
            ImportFavoritesDialog(
                onDismiss = { showImportDialog = false },
                onImport = { jsonString, onResult ->
                    coroutineScope.launch {
                        val result = settingsViewModel.importFavorites(jsonString)
                        onResult(result)
                    }
                }
            )
        }

        if (showUpdateDialog) {
            AlertDialog(
                onDismissRequest = { showUpdateDialog = false },
                title = { Text(text = "No Updates Found") },
                confirmButton = {
                    TextButton(onClick = { showUpdateDialog = false }) {
                        Text("Okay.")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 26.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    modifier = Modifier.size(64.dp),
                    contentDescription = "App Icon"
                )

                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Purdue keeps breaking things so this exists now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    )
}

@Composable
fun NavigationalSetting(
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value != null) {
                Text(
                    text = value,
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Icon(
                painter = painterResource(R.drawable.keyboard_arrow_right),
                contentDescription = null
            )
        }
    }
}

@Composable
fun InformationSetting(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ActionSetting(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ImportFavoritesDialog(
    onDismiss: () -> Unit,
    onImport: (String, (Result<Int>) -> Unit) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isImporting) onDismiss() },
        title = { Text("Import Favorites") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Paste the favorites JSON data below:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = jsonText,
                    onValueChange = {
                        jsonText = it
                        errorMessage = null
                        successMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = { Text("Paste JSON here...") },
                    isError = errorMessage != null,
                    enabled = !isImporting
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (successMessage != null) {
                    Text(
                        text = successMessage!!,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isImporting) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (successMessage != null) {
                        onDismiss()
                    } else if (jsonText.isNotBlank()) {
                        isImporting = true
                        errorMessage = null
                        onImport(jsonText) { result ->
                            isImporting = false
                            result.fold(
                                onSuccess = { count ->
                                    successMessage =
                                        "Successfully imported $count favorite${if (count != 1) "s" else ""}!"
                                },
                                onFailure = { exception ->
                                    errorMessage = "Import failed: ${exception.message}"
                                }
                            )
                        }
                    } else {
                        errorMessage = "Please paste JSON content"
                    }
                },
                enabled = !isImporting
            ) {
                Text(if (successMessage != null) "Done" else "Import")
            }
        },
        dismissButton = {
            if (successMessage == null) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isImporting
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    BetterPurdueDiningTheme(
        theme = "Dark"
    ) {
        SettingsScreen()
    }
}
