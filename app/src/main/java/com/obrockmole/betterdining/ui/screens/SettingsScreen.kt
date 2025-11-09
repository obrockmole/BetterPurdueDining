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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.obrockmole.betterdining.data.UserPreferencesRepository
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme
import com.obrockmole.betterdining.viewmodel.SettingsViewModel
import com.obrockmole.betterdining.viewmodel.SettingsViewModelFactory


@Composable
fun SettingsScreen(
    onNavigateToDefaultScreen: () -> Unit = {},
    onNavigateToLicensesScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(UserPreferencesRepository(context))
    )
    val defaultScreen by settingsViewModel.defaultScreen.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Settings",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                SettingsGroupDivider()
                SettingsSectionHeader(title = "Preferences")
            }
            item {
                NavigationalSetting(
                    title = "Default Screen",
                    value = defaultScreen,
                    onClick = onNavigateToDefaultScreen
                )
                SettingsDivider()
            }
            item {
                NavigationalSetting(
                    title = "Theme",
                    value = "Material",
                    onClick = {
                        // TODO: Themes
                    }
                )
                SettingsDivider()
            }
            item {
                NavigationalSetting(
                    title = "Navigation Style",
                    value = "Bottom",
                    onClick = {
                        // TODO: Alternate navigation
                    }
                )
            }

            item {
                SettingsGroupDivider()
                SettingsSectionHeader(title = "About")
            }
            item {
                NavigationalSetting(
                    title = "Licenses",
                    onClick = onNavigateToLicensesScreen
                )
                SettingsDivider()
            }
            item {
                InformationSetting(title = "Version", value = "0.6.9")
                SettingsDivider()
            }
            item {
                ActionSetting(
                    title = "Check For Updates",
                    onClick = {
                        // TODO: Check for updates
                    }
                )
                SettingsDivider()
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
                    painter = painterResource(id = R.drawable.app_icon),
                    modifier = Modifier.size(64.dp),
                    contentDescription = "App Icon"
                )

                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Purdue keeps fucking up so this exists now.",
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
fun SettingsDivider() {
    HorizontalDivider()
}

@Composable
fun SettingsGroupDivider() {
    HorizontalDivider(
        thickness = 6.dp
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

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    BetterPurdueDiningTheme {
        SettingsScreen()
    }
}
