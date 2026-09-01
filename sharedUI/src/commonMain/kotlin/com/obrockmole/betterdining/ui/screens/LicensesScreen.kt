package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import betterpurduedining.sharedui.generated.resources.Res
import betterpurduedining.sharedui.generated.resources.arrow_back
import com.obrockmole.betterdining.utils.Logger
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "LicensesScreen"

@Composable
fun LicensesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Licenses") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.align(BiasAlignment(0f, -0.15f)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Shhh...",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "🤫",
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }
    }
}
