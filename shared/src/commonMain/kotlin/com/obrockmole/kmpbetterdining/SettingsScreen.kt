package com.obrockmole.kmpbetterdining

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kmpbetterdining.shared.generated.resources.Res
import kmpbetterdining.shared.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToDefaultScreen: () -> Unit = {},
    onNavigateToTheme: () -> Unit = {},
    onNavigateToNavStyle: () -> Unit = {},
    onNavigateToLicensesScreen: () -> Unit = {},
    onNavigateToLogLevel: () -> Unit = {}
) {
    val defaultScreen by remember { mutableStateOf("Home") }
    val appTheme by remember { mutableStateOf("Dark") }
    val navStyle by remember { mutableStateOf("Bottom") }
    val logLevel by remember { mutableStateOf("Minimal") }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                SettingsSectionHeader(title = "Preferences")
            }

            item {
                NavigationalSetting(
                    title = "Default Screen",
                    value = defaultScreen,
                    onClick = {
                        onNavigateToDefaultScreen()
                    }
                )
                HorizontalDivider()
            }

            item {
                NavigationalSetting(
                    title = "Theme",
                    value = appTheme,
                    onClick = {
                        onNavigateToTheme()
                    }
                )
                HorizontalDivider()
            }

            item {
                NavigationalSetting(
                    title = "Navigation Style",
                    value = navStyle,
                    onClick = {
                        onNavigateToNavStyle()
                    }
                )
                HorizontalDivider()
            }

            item {
                NavigationalSetting(
                    title = "Logging",
                    value = logLevel,
                    onClick = {
                        onNavigateToLogLevel()
                    }
                )
            }

            item {
                HorizontalDivider(thickness = 6.dp)
                SettingsSectionHeader(title = "About")
            }

            item {
                NavigationalSetting(
                    title = "Licenses",
                    onClick = {
                        onNavigateToLicensesScreen()
                    }
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
                    onClick = {}
                )
                HorizontalDivider(thickness = 6.dp)
            }

            item {
                ActionSetting(
                    title = "Import Favorites",
                    onClick = {}
                )
                HorizontalDivider()
            }
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
                    painter = painterResource(Res.drawable.app_icon),
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
fun NavigationalSetting(title: String, value: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        if (value != null) {
            Text(
                text = value,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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