package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import betterpurduedining.sharedui.generated.resources.Res
import betterpurduedining.sharedui.generated.resources.arrow_back
import com.obrockmole.betterdining.utils.Logger
import com.obrockmole.betterdining.utils.Platform
import com.obrockmole.betterdining.utils.currentPlatform
import com.obrockmole.betterdining.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "ThemeSelectionScreen"

@Composable
fun ThemeSelectionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val appTheme by settingsViewModel.appTheme.collectAsState()

    val themeOptions = remember {
        if (currentPlatform == Platform.ANDROID) {
            listOf("Material", "Dark", "Light", "Rainbow")
        } else {
            listOf("Dark", "Light", "Rainbow")
        }
    }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Select App Theme") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
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
                                    Logger.LogInfo(LOG_TAG, "Changed app theme from $appTheme to $theme")
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
                                    Logger.LogInfo(LOG_TAG, "Changed app theme from $appTheme to $theme")
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