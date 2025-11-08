package com.aivantage.micromovementguidetv

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme
import java.util.concurrent.TimeUnit

private const val KILL_SWITCH_PRESS_COUNT = 5
private const val KILL_SWITCH_INTERVAL_MS = 1000L // 1 second
private const val EXERCISE_WORK_TAG = "exerciseWork"

class MainActivity : ComponentActivity() {

    private var killSwitchPresses = 0
    private var lastKillSwitchPressTime = 0L

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle the back button press for the kill switch
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val now = System.currentTimeMillis()
                if (now - lastKillSwitchPressTime < KILL_SWITCH_INTERVAL_MS) {
                    killSwitchPresses++
                } else {
                    killSwitchPresses = 1
                }
                lastKillSwitchPressTime = now

                if (killSwitchPresses >= KILL_SWITCH_PRESS_COUNT) {
                    cancelExerciseWorker(this@MainActivity)
                    setContent {
                        MicroMovementGuideTheme {
                            KillSwitchScreen()
                        }
                    }
                } else {
                    // If the kill switch is not activated, perform the default back action
                    if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })

        setContent {
            MicroMovementGuideTheme {
                val context = LocalContext.current
                var hasOnboarded by remember { mutableStateOf(SettingsManager.hasOnboarded(context)) }
                var appSettings by remember { mutableStateOf(SettingsManager.getSettings(context)) }
                var currentScreen by remember { mutableStateOf("Main") }
                var isKilled by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    if (isKilled) {
                        KillSwitchScreen()
                    } else if (!hasOnboarded) {
                        OnboardingScreen(onOnboardingComplete = {
                            SettingsManager.setHasOnboarded(context, true)
                            hasOnboarded = true
                            scheduleExerciseWorker(context, appSettings)
                        })
                    } else {
                        when (currentScreen) {
                            "Main" -> MainScreen(onSettingsClicked = { currentScreen = "Settings" })
                            "Settings" -> SettingsScreen(
                                initialSettings = appSettings,
                                onSave = { newSettings ->
                                    SettingsManager.saveSettings(context, newSettings)
                                    appSettings = newSettings
                                    currentScreen = "Main"
                                    // Reschedule the worker with the new settings
                                    scheduleExerciseWorker(context, newSettings)
                                },
                                onClose = { currentScreen = "Main" }
                            )
                        }
                    }
                }
            }
        }
        // Start the worker if already onboarded
        if (SettingsManager.hasOnboarded(this)) {
            scheduleExerciseWorker(this, SettingsManager.getSettings(this))
        }
    }

    private fun scheduleExerciseWorker(context: Context, appSettings: AppSettings) {
        val workManager = WorkManager.getInstance(context)
        val workRequest = PeriodicWorkRequestBuilder<ExerciseWorker>(
            appSettings.breakInterval.toLong(),
            TimeUnit.MINUTES
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            EXERCISE_WORK_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelExerciseWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(EXERCISE_WORK_TAG)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(onSettingsClicked: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Micro-Movement Guide is active.")
        IconButton(
            onClick = onSettingsClicked,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .focusRequester(focusRequester)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun KillSwitchScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Micro-Movement Guide has been paused.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please restart the app to enable it again.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MicroMovementGuideTheme {
        MainScreen(onSettingsClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun KillSwitchScreenPreview() {
    MicroMovementGuideTheme {
        KillSwitchScreen()
    }
}
