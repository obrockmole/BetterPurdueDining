package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import betterpurduedining.sharedui.generated.resources.Res
import betterpurduedining.sharedui.generated.resources.app_icon
import betterpurduedining.sharedui.generated.resources.keyboard_arrow_right
import com.obrockmole.betterdining.ui.HeaderBar
import com.obrockmole.betterdining.utils.Logger
import com.obrockmole.betterdining.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private const val LOG_TAG = "SettingsScreen"
private const val CURRENT_VERSION = "2.0.0"

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToDefaultScreen: () -> Unit = {},
    onNavigateToTheme: () -> Unit = {},
    onNavigateToNavStyle: () -> Unit = {},
    onNavigateToLicensesScreen: () -> Unit = {},
    onNavigateToLogAmount: () -> Unit = {},
    onOpenDrawer: (() -> Unit)?,
    settingsViewModel: SettingsViewModel,
) {
    Logger.LogDebug(LOG_TAG, "Composable loaded")

    val defaultScreen by settingsViewModel.defaultScreen.collectAsState()
    val appTheme by settingsViewModel.appTheme.collectAsState()
    val navStyle by settingsViewModel.navStyle.collectAsState()
    val logAmount by settingsViewModel.logAmount.collectAsState()

    val latestVersion by settingsViewModel.latestVersion.collectAsState()
    val latestVersionURL by settingsViewModel.latestVersionURL.collectAsState()

    var showUpdateDialog by remember { mutableStateOf(false) }
    var checkingForUpdates by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            HeaderBar(
                title = "Settings",
                onOpenDrawer = onOpenDrawer
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                        value = logAmount,
                        onClick = {
                            Logger.LogInfo(LOG_TAG, "Navigating to log amount settings")
                            onNavigateToLogAmount()
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
                    if (checkingForUpdates) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        ActionSetting(
                            title = "Check For Updates",
                            onClick = {
                                Logger.LogDebug(LOG_TAG, "Check for updates attempt")
                                coroutineScope.launch {
                                    checkingForUpdates = true
                                    settingsViewModel.getLatestVersion()
                                    checkingForUpdates = false
                                    showUpdateDialog = true
                                }
                            }
                        )
                    }
                    HorizontalDivider(thickness = 6.dp)
                }
            }

            if (showUpdateDialog) {
                if (latestVersion == null || latestVersionURL == null) {
                    AlertDialog(
                        title = { Text(text = "Error") },
                        text = {
                            Text(
                                text = "Error checking for updates. Please try again later.",
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { showUpdateDialog = false }) {
                                Text("Close")
                            }
                        },
                        onDismissRequest = { showUpdateDialog = false }
                    )
                } else if (latestVersion != CURRENT_VERSION) {
                    AlertDialog(
                        title = { Text(text = "Update Found") },
                        text = {
                            Text(
                                text = "Current Version: $CURRENT_VERSION\nLatest Version: $latestVersion",
                            )
                        },
                        dismissButton = {
                            TextButton(onClick = { showUpdateDialog = false }) {
                                Text("Close")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showUpdateDialog = false
                                val downloadUrl =
                                    if (latestVersionURL != "") latestVersionURL!! else "https://github.com/obrockmole/BetterPurdueDining/releases"
                                Logger.LogInfo(LOG_TAG, "Opening latest version download link ($downloadUrl)")
                                uriHandler.openUri(downloadUrl)
                            }) {
                                Text("Download")
                            }
                        },
                        onDismissRequest = { showUpdateDialog = false }
                    )
                } else {
                    AlertDialog(
                        title = { Text(text = "Not Updates Found") },
                        text = {
                            Text(
                                text = "You are already on the latest version v$latestVersion",
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { showUpdateDialog = false }) {
                                Text("Okay")
                            }
                        },
                        onDismissRequest = { showUpdateDialog = false }
                    )
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