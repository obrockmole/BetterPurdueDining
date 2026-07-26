package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.utils.Logger
import com.obrockmole.kmpbetterdining.viewmodel.SettingsViewModel
import kmpbetterdining.shared.generated.resources.Res
import kmpbetterdining.shared.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "DefaultScreenSelectionScreen"

@Composable
fun DefaultScreenSelectionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val defaultScreen by settingsViewModel.defaultScreen.collectAsState()

    val screenOptions = listOf("Home", "Favorites")
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Change Default Screen") },
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
                screenOptions.forEachIndexed { index, screen ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (defaultScreen != screen) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed default screen from $defaultScreen to $screen")
                                    settingsViewModel.setDefaultScreen(screen)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = screen,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        RadioButton(
                            selected = defaultScreen == screen,
                            onClick = {
                                if (defaultScreen != screen) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed default screen from $defaultScreen to $screen")
                                    settingsViewModel.setDefaultScreen(screen)
                                }
                            }
                        )
                    }

                    if (index < screenOptions.size - 1) {
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