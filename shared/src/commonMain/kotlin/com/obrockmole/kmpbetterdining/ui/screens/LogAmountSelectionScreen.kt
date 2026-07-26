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

private const val LOG_TAG = "LogAmountSelectionScreen"

@Composable
fun LogAmountSelectionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val logAmount by settingsViewModel.logAmount.collectAsState()

    val amountOptions = listOf("Full", "Minimal", "Off")
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
                amountOptions.forEachIndexed { index, amount ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (logAmount != amount) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed log amount from $logAmount to $amount")
                                    settingsViewModel.setLogAmount(amount)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = amount,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        RadioButton(
                            selected = logAmount == amount,
                            onClick = {
                                if (logAmount != amount) {
                                    loading = true
                                    onNavigateBack()
                                    Logger.LogInfo(LOG_TAG, "Changed log amount from $logAmount to $amount")
                                    settingsViewModel.setLogAmount(amount)
                                }
                            }
                        )
                    }

                    if (index < amountOptions.size - 1) {
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