package com.aivantage.micromovementguidetv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialSettings: AppSettings,
    onSave: (AppSettings) -> Unit,
    onClose: () -> Unit
) {
    var tempSettings by remember { mutableStateOf(initialSettings) }
    val saveButtonFocusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Settings", style = androidx.tv.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // Time Between Breaks
        SettingItem(
            label = "Time Between Breaks",
            value = "${tempSettings.breakInterval} minutes",
            onDecrease = { tempSettings = tempSettings.copy(breakInterval = (tempSettings.breakInterval - 5).coerceAtLeast(5)) },
            onIncrease = { tempSettings = tempSettings.copy(breakInterval = (tempSettings.breakInterval + 5).coerceAtMost(60)) }
        )

        // Length of Break
        SettingItem(
            label = "Length of Break",
            value = "${tempSettings.exerciseDuration} minutes",
            onDecrease = { tempSettings = tempSettings.copy(exerciseDuration = (tempSettings.exerciseDuration - 1).coerceAtLeast(1)) },
            onIncrease = { tempSettings = tempSettings.copy(exerciseDuration = (tempSettings.exerciseDuration + 1).coerceAtMost(10)) }
        )

        // Condition Focus
        SettingItem(
            label = "Condition Focus",
            value = tempSettings.condition,
            onDecrease = {
                val currentIndex = CONDITIONS.indexOf(tempSettings.condition)
                val nextIndex = (currentIndex - 1 + CONDITIONS.size) % CONDITIONS.size
                tempSettings = tempSettings.copy(condition = CONDITIONS[nextIndex])
            },
            onIncrease = {
                val currentIndex = CONDITIONS.indexOf(tempSettings.condition)
                val nextIndex = (currentIndex + 1) % CONDITIONS.size
                tempSettings = tempSettings.copy(condition = CONDITIONS[nextIndex])
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Button(
                onClick = { onSave(tempSettings) },
                modifier = Modifier.focusRequester(saveButtonFocusRequester)
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onClose) {
                Text("Cancel")
            }
        }
    }

    LaunchedEffect(Unit) {
        saveButtonFocusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingItem(label: String, value: String, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.width(200.dp))
        Button(onClick = onDecrease) { Text("-") }
        Text(value, modifier = Modifier.padding(horizontal = 16.dp))
        Button(onClick = onIncrease) { Text("+") }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MicroMovementGuideTheme {
        SettingsScreen(initialSettings = AppSettings(), onSave = {}, onClose = {})
    }
}
