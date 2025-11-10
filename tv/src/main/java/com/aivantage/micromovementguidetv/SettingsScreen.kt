package com.aivantage.micromovementguidetv

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aivantage.micromovementguidetv.ui.theme.AppTheme
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialSettings: AppSettings,
    onSave: (AppSettings) -> Unit,
    onClose: () -> Unit,
    onAboutClicked: () -> Unit
) {
    var tempSettings by remember { mutableStateOf(initialSettings) }
    val saveButtonFocusRequester = remember { FocusRequester() }
    val allExercises = remember { ExerciseDatabase.getAllExerciseDefinitions() }
    var showSaveIndicator by remember { mutableStateOf(false) }

    val fontSizeOptions = listOf(
        "Normal" to 1.0f,
        "Large" to 1.2f,
        "Extra Large" to 1.4f
    )

    LaunchedEffect(tempSettings) {
        if (tempSettings != initialSettings) {
            delay(30000) // 30 seconds
            onSave(tempSettings)
            showSaveIndicator = true
            delay(2000) // Show indicator for 2 seconds
            showSaveIndicator = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        if (showSaveIndicator) {
            Text("Settings Saved!", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
        }

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

        MultiSelectSettingItem(
            label = "Disliked Exercises",
            allExercises = allExercises,
            selectedExerciseIds = tempSettings.dislikedExerciseIds,
            onSelectionChanged = { newSelection ->
                tempSettings = tempSettings.copy(dislikedExerciseIds = newSelection)
            }
        )

        PickerSettingItem(
            label = "Font Size",
            value = fontSizeOptions.first { it.second == tempSettings.fontSizeMultiplier }.first,
            onValueChange = { direction ->
                val currentIndex = fontSizeOptions.indexOfFirst { it.second == tempSettings.fontSizeMultiplier }
                val nextIndex = (currentIndex + direction + fontSizeOptions.size) % fontSizeOptions.size
                tempSettings = tempSettings.copy(fontSizeMultiplier = fontSizeOptions[nextIndex].second)
            }
        )

        PickerSettingItem(
            label = "App Theme",
            value = tempSettings.appTheme.themeName,
            onValueChange = { direction ->
                val currentThemeIndex = AppTheme.entries.indexOf(tempSettings.appTheme)
                val nextIndex = (currentThemeIndex + direction + AppTheme.entries.size) % AppTheme.entries.size
                tempSettings = tempSettings.copy(appTheme = AppTheme.entries[nextIndex])
            }
        )

        PickerSettingItem(
            label = "App Enabled",
            value = if (tempSettings.isAppEnabled) "On" else "Off",
            onValueChange = { _ ->
                tempSettings = tempSettings.copy(isAppEnabled = !tempSettings.isAppEnabled)
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
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onAboutClicked) {
                Text("About")
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
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .focusRequester(remember { FocusRequester() })
            .focusable(interactionSource = interactionSource)
            .background(if (isFocused) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
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
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal))
        }

        Spacer(modifier = Modifier.width(32.dp))

        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isFocused) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Decrease")
            }
            Text(value, modifier = Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal))
            if (isFocused) {
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Increase")
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MultiSelectSettingItem(
    label: String,
    allExercises: List<ExerciseDefinition>,
    selectedExerciseIds: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable(interactionSource = interactionSource)
                .background(if (isFocused || expanded) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key.nativeKeyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) {
                        expanded = !expanded
                        return@onKeyEvent true
                    }
                    false
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.End
            ) {
                Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal))
            }

            Spacer(modifier = Modifier.width(32.dp))

            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (selectedExerciseIds.isEmpty()) "None selected" else "${selectedExerciseIds.size} selected",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal)
                )
            }
        }

        if (expanded) {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(start = 32.dp)
                ) {
                    items(allExercises) { exercise ->
                        val isSelected = exercise.id in selectedExerciseIds
                        val itemFocusRequester = remember { FocusRequester() }
                        val itemInteractionSource = remember { MutableInteractionSource() }
                        val itemIsFocused by itemInteractionSource.collectIsFocusedAsState()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .focusRequester(itemFocusRequester)
                                .focusable(interactionSource = itemInteractionSource)
                                .background(if (itemIsFocused) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                .onKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyDown && event.key.nativeKeyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) {
                                        val newSelection = if (isSelected) {
                                            selectedExerciseIds - exercise.id
                                        } else {
                                            selectedExerciseIds + exercise.id
                                        }
                                        onSelectionChanged(newSelection)
                                        return@onKeyEvent true
                                    }
                                    false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = if (isSelected) "Selected" else "Not Selected",
                                tint = if (isSelected) Color.Green else Color.Red,
                                modifier = Modifier.height(24.dp).width(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = exercise.name,
                                style = (if (itemIsFocused) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium).copy(fontWeight = FontWeight.Normal)
                            )
                        }
                    }
                }
                Button(onClick = { expanded = false }, modifier = Modifier.padding(start = 32.dp)) {
                    Text("Done")
                }
            }
        }
    }

    LaunchedEffect(expanded) {
        if (!expanded) {
            focusRequester.requestFocus()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MicroMovementGuideTheme(appSettings = AppSettings()) {
        SettingsScreen(initialSettings = AppSettings(), onSave = {}, onClose = {}, onAboutClicked = {})
    }
}
