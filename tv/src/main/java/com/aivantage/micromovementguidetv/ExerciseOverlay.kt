package com.aivantage.micromovementguidetv

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ExerciseOverlay(appSettings: AppSettings, onComplete: () -> Unit) {
    var view by rememberSaveable { mutableStateOf("prompt") }
    val backgroundAlpha = if (appSettings.isHighContrast) 0.9f else 0.75f
    val context = LocalContext.current

    MicroMovementGuideTheme(appSettings = appSettings) { // Pass appSettings to the theme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = backgroundAlpha))
        ) {
            when (view) {
                "prompt" -> {
                    PromptView(
                        appSettings = appSettings,
                        onStart = { view = "exercising" },
                        onDismiss = onComplete,
                        onSnooze = {
                            scheduleSnooze(context, onComplete)
                        }
                    )
                }
                "exercising" -> {
                    ExercisingView(
                        appSettings = appSettings,
                        onComplete = { view = "finished" }
                    )
                }
                "finished" -> {
                    FinishedView(appSettings = appSettings, onComplete = onComplete)
                }
            }
        }
    }
}

private fun scheduleSnooze(context: Context, onComplete: () -> Unit) {
    val workManager = WorkManager.getInstance(context)
    val workRequest = OneTimeWorkRequestBuilder<ExerciseWorker>()
        .setInitialDelay(10, TimeUnit.MINUTES)
        .build()

    workManager.enqueue(workRequest)
    onComplete()
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PromptView(
    appSettings: AppSettings,
    onStart: () -> Unit,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val okButtonFocusRequester = remember { FocusRequester() }
    val fontWeight = if (appSettings.isHighContrast) FontWeight.Bold else FontWeight.Normal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Time for a movement break!", style = MaterialTheme.typography.headlineLarge, fontWeight = fontWeight)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Let's do a few gentle exercises to keep you moving.", style = MaterialTheme.typography.bodyLarge, fontWeight = fontWeight)
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Button(
                onClick = onStart,
                modifier = Modifier.focusRequester(okButtonFocusRequester)
            ) {
                Text("OK")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onSnooze) {
                Text("Snooze")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onDismiss) {
                Text("DISMISS")
            }
        }
    }

    LaunchedEffect(Unit) {
        okButtonFocusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ExercisingView(appSettings: AppSettings, onComplete: () -> Unit) {
    val exercisePack = remember { ExerciseDatabase.getExercisePackForCondition(appSettings.condition, appSettings) }
    val steps = remember { exercisePack?.steps ?: emptyList() }
    var currentStepIndex by rememberSaveable { mutableIntStateOf(0) }
    var timeLeft by rememberSaveable { mutableIntStateOf(0) }
    var progress by rememberSaveable { mutableFloatStateOf(1f) }
    var isPaused by rememberSaveable { mutableStateOf(false) }
    val pauseButtonFocusRequester = remember { FocusRequester() }
    val fontWeight = if (appSettings.isHighContrast) FontWeight.Bold else FontWeight.Normal

    LaunchedEffect(key1 = currentStepIndex, key2 = isPaused) {
        if (isPaused) {
            return@LaunchedEffect
        }

        val duration = steps.getOrNull(currentStepIndex)?.duration ?: 0
        if (timeLeft == 0) {
            timeLeft = duration
        }

        while (timeLeft > 0) {
            progress = timeLeft.toFloat() / duration
            delay(1000)
            if (!isPaused) {
                timeLeft--
            }
        }

        if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
        } else {
            onComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = currentStepIndex, animationSpec = tween(500)) { stepIndex ->
            val currentStep = steps.getOrNull(stepIndex)
            val exerciseDefinition = remember(currentStep?.exerciseId) {
                currentStep?.let { ExerciseDatabase.getDefinitionById(it.exerciseId) }
            }

            if (exerciseDefinition != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = exerciseDefinition.name, style = MaterialTheme.typography.headlineLarge, fontWeight = fontWeight)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(220.dp),
                            strokeWidth = 8.dp
                        )
                        if (exerciseDefinition.lottieAnimationName != null) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.Asset(exerciseDefinition.lottieAnimationName)
                            )
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(200.dp)
                            )
                        } else {
                            val drawableId = getDrawableIdByName(exerciseDefinition.iconName)
                            if (drawableId != null) {
                                Icon(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = exerciseDefinition.name,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = exerciseDefinition.instruction, style = MaterialTheme.typography.bodyLarge, fontWeight = fontWeight, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Keep going...", style = MaterialTheme.typography.bodyLarge, fontWeight = fontWeight)
                    Spacer(modifier = Modifier.height(32.dp))

                    Row {
                        Button(
                            onClick = { isPaused = !isPaused },
                            modifier = Modifier.focusRequester(pauseButtonFocusRequester)
                        ) {
                            Text(if (isPaused) "Resume" else "Pause")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = {
                            if (currentStepIndex < steps.size - 1) {
                                currentStepIndex++
                                timeLeft = 0
                            } else {
                                onComplete()
                            }
                        }) {
                            Text("Skip")
                        }
                    }
                }
            }
        }
        Footer()
    }


    LaunchedEffect(Unit) {
        pauseButtonFocusRequester.requestFocus()
    }
}

// Compile-time safe mapping for drawable resources
private fun getDrawableIdByName(iconName: String): Int? {
    return when (iconName) {
        "ic_neck_stretch" -> R.drawable.ic_neck_stretch
        "ic_hand_clench" -> R.drawable.ic_hand_clench
        "ic_ankle_rotation" -> R.drawable.ic_ankle_rotation
        "ic_shoulder_roll" -> R.drawable.ic_shoulder_roll
        "ic_seated_marching" -> R.drawable.ic_seated_marching
        "ic_wrist_bends" -> R.drawable.ic_wrist_bends
        "ic_deep_breathing" -> R.drawable.ic_deep_breathing
        else -> null
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = "Hint: Press the back button 5 times quickly to pause the guide.",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FinishedView(appSettings: AppSettings, onComplete: () -> Unit) {
    val fontWeight = if (appSettings.isHighContrast) FontWeight.Bold else FontWeight.Normal
    LaunchedEffect(Unit) {
        delay(3000)
        onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Well done!", style = MaterialTheme.typography.headlineLarge, fontWeight = fontWeight)
        Spacer(modifier = Modifier.height(16.dp))
        Text("You're doing great. See you next time.", style = MaterialTheme.typography.bodyLarge, fontWeight = fontWeight)
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseOverlayPreview() {
    MicroMovementGuideTheme(appSettings = AppSettings(condition = "General Wellness")) {
        ExerciseOverlay(appSettings = AppSettings(condition = "General Wellness"), onComplete = {})
    }
}
