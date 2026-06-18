package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrockmole.kmpbetterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.kmpbetterdining.utils.Logger
import kmpbetterdining.shared.generated.resources.Res
import kmpbetterdining.shared.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "LogLevelSelectionScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogLevelSelectionScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val levelOptions = listOf("Full", "Minimal", "Off")
    val logLevel by remember { mutableStateOf("Minimal") }

    var loading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Select Logging Amount") },
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
                levelOptions.forEachIndexed { index, level ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (logLevel != level) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed log level from $logLevel to $level")
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = level,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        RadioButton(
                            selected = logLevel == level,
                            onClick = {
                                if (logLevel != level) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed log level from $logLevel to $level")
                                }
                            }
                        )
                    }

                    if (index < levelOptions.size - 1) {
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
fun LogLevelSelectionScreenPreview() {
    BetterPurdueDiningTheme(
        theme = "Dark"
    ) {
        LogLevelSelectionScreen(onNavigateBack = {})
    }
}
