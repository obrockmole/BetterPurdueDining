package com.obrockmole.kmpbetterdining.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.obrockmole.kmpbetterdining.utils.Logger
import kmpbetterdining.shared.generated.resources.Res
import kmpbetterdining.shared.generated.resources.app_icon
import kmpbetterdining.shared.generated.resources.keyboard_arrow_right
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "SettingsScreen"
private const val CURRENT_VERSION = "1.4.1"

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToDefaultScreen: () -> Unit = {},
    onNavigateToTheme: () -> Unit = {},
    onNavigateToNavStyle: () -> Unit = {},
    onNavigateToLicensesScreen: () -> Unit = {},
    onNavigateToLogLevel: () -> Unit = {}
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val defaultScreen by remember { mutableStateOf("Home") }
    val appTheme by remember { mutableStateOf("Dark") }
    val navStyle by remember { mutableStateOf("Bottom") }
    val logLevel by remember { mutableStateOf("Minimal") }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                    onClick = {
                        Logger.LogInfo(LOG_TAG, "Navigating to default screen settings")
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
                        Logger.LogInfo(LOG_TAG, "Navigating to theme settings")
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
                        Logger.LogInfo(LOG_TAG, "Navigating to nav style settings")
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
                        Logger.LogInfo(LOG_TAG, "Navigating to log level settings")
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
                        Logger.LogInfo(LOG_TAG, "Navigating to licenses")
                        onNavigateToLicensesScreen()
                    }
                )
                HorizontalDivider()
            }

            item {
                InformationSetting(title = "Version", value = CURRENT_VERSION)
                HorizontalDivider()
            }

            item {
                ActionSetting(
                    title = "Check For Updates",
                    onClick = {
                        Logger.LogDebug(LOG_TAG, "Check for updates attempt")
                    }
                )
                HorizontalDivider(thickness = 6.dp)
            }

            item {
                ActionSetting(
                    title = "Import Favorites",
                    onClick = {
                        Logger.LogDebug(LOG_TAG, "Import favorites attempt")
                    }
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
                painter = painterResource(Res.drawable.keyboard_arrow_right),
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