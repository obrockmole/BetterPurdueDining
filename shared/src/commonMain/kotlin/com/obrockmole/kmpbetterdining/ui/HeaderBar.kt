package com.obrockmole.kmpbetterdining.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import kmpbetterdining.shared.generated.resources.Res
import kmpbetterdining.shared.generated.resources.menu
import org.jetbrains.compose.resources.painterResource

@Composable
fun HeaderBar(
    title: String,
    onOpenDrawer: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column {
        TopAppBar(
            title = { Text(text = title, style = MaterialTheme.typography.headlineLargeEmphasized) },
            navigationIcon = {
                if (onOpenDrawer != null) {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(painterResource(Res.drawable.menu), contentDescription = "Menu")
                    }
                }
            },
            actions = actions
        )
        HorizontalDivider()
    }
}