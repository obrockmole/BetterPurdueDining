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
import com.obrockmole.betterdining.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "NavStyleSelectionScreen"

@Composable
fun NavStyleSelectionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val navStyle by settingsViewModel.navStyle.collectAsState()

    val styleOptions = listOf("Bottom", "Side")
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Select Navigation Style") },
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
                styleOptions.forEachIndexed { index, style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (navStyle != style) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed nav style from $navStyle to $style")
                                    settingsViewModel.setNavStyle(style)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = style,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        RadioButton(
                            selected = navStyle == style,
                            onClick = {
                                if (navStyle != style) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed nav style from $navStyle to $style")
                                    settingsViewModel.setNavStyle(style)
                                }
                            }
                        )
                    }

                    if (index < styleOptions.size - 1) {
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
