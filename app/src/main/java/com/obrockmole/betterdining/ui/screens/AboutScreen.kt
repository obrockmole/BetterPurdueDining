package com.obrockmole.betterdining.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrockmole.betterdining.ui.theme.BetterPurdueDiningTheme


@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Column(
            modifier = Modifier.align(BiasAlignment(0f, -0.25f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Purdue keeps fucking up so this exists now",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 26.dp, end = 26.dp, bottom = 8.dp)
            )
            Text(
                text = "Version 1.0.0"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    BetterPurdueDiningTheme {
        AboutScreen()
    }
}