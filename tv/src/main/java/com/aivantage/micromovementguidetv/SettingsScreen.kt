package com.aivantage.micromovementguidetv

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
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
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        PickerSettingItem(
            label = "Time Between Breaks",
            value = "${tempSettings.breakInterval} minutes",
            onValueChange = { direction ->
                val newInterval = (tempSettings.breakInterval + direction * 5).coerceIn(5, 60)
                tempSettings = tempSettings.copy(breakInterval = newInterval)
            }
        )

        PickerSettingItem(
            label = "Length of Break",
            value = "${tempSettings.exerciseDuration} minutes",
            onValueChange = { direction ->
                val newDuration = (tempSettings.exerciseDuration + direction).coerceIn(1, 10)
                tempSettings = tempSettings.copy(exerciseDuration = newDuration)
            }
        )

        PickerSettingItem(
            label = "Condition Focus",
            value = tempSettings.condition,
            onValueChange = { direction ->
                val currentIndex = CONDITIONS.indexOf(tempSettings.condition)
                val nextIndex = (currentIndex + direction + CONDITIONS.size) % CONDITIONS.size
                tempSettings = tempSettings.copy(condition = CONDITIONS[nextIndex])
            }
        )

        PickerSettingItem(
            label = "High Contrast Mode",
            value = if (tempSettings.isHighContrast) "On" else "Off",
            onValueChange = { _ ->
                tempSettings = tempSettings.copy(isHighContrast = !tempSettings.isHighContrast)
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
fun PickerSettingItem(
    label: String,
    value: String,
    onValueChange: (Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .focusRequester(remember { FocusRequester() })
            .focusable(interactionSource = interactionSource)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key.nativeKeyCode) {
                        android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onValueChange(-1)
                            return@onKeyEvent true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onValueChange(1)
                            return@onKeyEvent true
                        }
                    }
                }
                false
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.width(200.dp), style = if (isFocused) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium)
        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Decrease")
        Text(value, modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyLarge)
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Increase")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MicroMovementGuideTheme {
        SettingsScreen(initialSettings = AppSettings(), onSave = {}, onClose = {})
    }
}
