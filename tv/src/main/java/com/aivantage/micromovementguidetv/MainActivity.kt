package com.aivantage.micromovementguidetv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme
import com.google.common.util.concurrent.ListenableFuture
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
                        MicroMovementGuideTheme(appSettings = SettingsManager.getSettings(this@MainActivity)) {
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
            val context = LocalContext.current
            var hasOnboarded by remember { mutableStateOf(SettingsManager.hasOnboarded(context)) }
            var appSettings by remember { mutableStateOf(SettingsManager.getSettings(context)) }
            var currentScreen by remember { mutableStateOf("Main") }
            var isKilled by remember { mutableStateOf(false) }

            MicroMovementGuideTheme(appSettings = appSettings) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    if (isKilled) {
                        KillSwitchScreen()
                    } else if (!hasOnboarded) {
                        OnboardingScreen(onOnboardingComplete = { newSettings ->
                            SettingsManager.saveSettings(context, newSettings)
                            SettingsManager.setHasOnboarded(context, true)
                            appSettings = newSettings
                            hasOnboarded = true
                            scheduleExerciseWorker(context, newSettings)
                        })
                    } else if (!appSettings.isAppEnabled) {
                        AppDisabledScreen(onEnableApp = {
                            val updatedSettings = appSettings.copy(isAppEnabled = true)
                            SettingsManager.saveSettings(context, updatedSettings)
                            appSettings = updatedSettings
                            scheduleExerciseWorker(context, updatedSettings)
                        })
                    }
                    else {
                        when (currentScreen) {
                            "Main" -> MainScreen(
                                appSettings = appSettings,
                                onSettingsClicked = { currentScreen = "Settings" }
                            )
                            "Settings" -> SettingsScreen(
                                initialSettings = appSettings,
                                onSave = { newSettings ->
                                    SettingsManager.saveSettings(context, newSettings)
                                    appSettings = newSettings
                                    currentScreen = "Main"
                                    // Reschedule the worker with the new settings
                                    scheduleExerciseWorker(context, newSettings)
                                },
                                onClose = { currentScreen = "Main" },
                                onAboutClicked = { currentScreen = "About" } // Pass callback for About screen
                            )
                            "About" -> AboutScreen(onBack = { currentScreen = "Settings" }) // New About screen
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

        if (!appSettings.isAppEnabled) {
            cancelExerciseWorker(context)
            return
        }

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
fun MainScreen(appSettings: AppSettings, onSettingsClicked: () -> Unit) {
    val context = LocalContext.current
    var nextBreakText by remember { mutableStateOf("Loading...") }
    val focusRequester = remember { FocusRequester() }

    fun startExercise() {
        val serviceIntent = Intent(context, ExerciseService::class.java).apply {
            putExtra("appSettings", appSettings)
        }
        context.startService(serviceIntent)
    }

    LaunchedEffect(Unit) {
        val workManager = WorkManager.getInstance(context)
        val workInfosFuture: ListenableFuture<List<WorkInfo>> = workManager.getWorkInfosForUniqueWork(EXERCISE_WORK_TAG)

        workInfosFuture.addListener({
            val workInfos = workInfosFuture.get()
            if (workInfos.isNotEmpty()) {
                val workInfo = workInfos[0]
                val nextRunTime = workInfo.nextScheduleTimeMillis
                val currentTime = System.currentTimeMillis()
                val minutesUntilNext = TimeUnit.MILLISECONDS.toMinutes(nextRunTime - currentTime)
                nextBreakText = "Your next movement break is in about $minutesUntilNext minutes."
            } else {
                nextBreakText = "No breaks scheduled. Check settings."
            }
        }, context.mainExecutor)

        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = nextBreakText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Button(
                onClick = { startExercise() },
                modifier = Modifier.focusRequester(focusRequester)
            ) {
                Text("Start Now")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onSettingsClicked
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Settings")
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppDisabledScreen(onEnableApp: () -> Unit) {
    val enableButtonFocusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Micro-Movement Guide is currently disabled.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "To re-enable, click the button below.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onEnableApp,
            modifier = Modifier.focusRequester(enableButtonFocusRequester)
        ) {
            Text("Enable App")
        }
    }

    LaunchedEffect(Unit) {
        enableButtonFocusRequester.requestFocus()
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
    MicroMovementGuideTheme(appSettings = AppSettings()) {
        MainScreen(appSettings = AppSettings(), onSettingsClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun KillSwitchScreenPreview() {
    MicroMovementGuideTheme(appSettings = AppSettings()) {
        KillSwitchScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun AppDisabledScreenPreview() {
    MicroMovementGuideTheme(appSettings = AppSettings()) {
        AppDisabledScreen(onEnableApp = {})
    }
}
