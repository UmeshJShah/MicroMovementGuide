package com.aivantage.micromovementguidetv

import android.content.Context
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ExerciseOverlay(appSettings: AppSettings, onComplete: () -> Unit) {
    var view by rememberSaveable { mutableStateOf("prompt") }
    val backgroundAlpha = if (appSettings.isHighContrast) 0.9f else 0.75f

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
                    onDismiss = onComplete
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PromptView(appSettings: AppSettings, onStart: () -> Unit, onDismiss: () -> Unit) {
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
    val exercisePack = remember { ExerciseDatabase.getExercisePackForCondition(appSettings.condition) }
    val steps = remember { exercisePack?.steps ?: emptyList() }
    var currentStepIndex by rememberSaveable { mutableStateOf(0) }
    var timeLeft by rememberSaveable { mutableStateOf(0) }
    var isPaused by rememberSaveable { mutableStateOf(false) }
    val pauseButtonFocusRequester = remember { FocusRequester() }
    val fontWeight = if (appSettings.isHighContrast) FontWeight.Bold else FontWeight.Normal

    LaunchedEffect(key1 = currentStepIndex, key2 = isPaused) {
        if (isPaused) {
            return@LaunchedEffect
        }

        // Set the time for the current step if it's a new step
        if (timeLeft == 0) {
            timeLeft = steps.getOrNull(currentStepIndex)?.duration ?: 0
        }

        // Run the countdown
        while (timeLeft > 0) {
            delay(1000)
            if (!isPaused) {
                timeLeft--
            }
        }

        // When the countdown is done, advance to the next step or finish
        if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
        } else {
            onComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (steps.isNotEmpty() && currentStepIndex < steps.size) {
            val currentStep = steps[currentStepIndex]
            val exerciseDefinition = remember(currentStep.exerciseId) {
                ExerciseDatabase.getDefinitionById(currentStep.exerciseId)
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
                        val drawableId = getDrawableId(LocalContext.current, exerciseDefinition.iconName)
                        if (drawableId != null) {
                            Icon(
                                painter = painterResource(id = drawableId),
                                contentDescription = exerciseDefinition.name,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = exerciseDefinition.instruction, style = MaterialTheme.typography.bodyLarge, fontWeight = fontWeight)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Time left: ${formatTime(timeLeft)}", style = MaterialTheme.typography.bodyLarge, fontWeight = fontWeight)
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
                                timeLeft = 0 // Reset timer to trigger LaunchedEffect correctly
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

private fun getDrawableId(context: Context, iconName: String): Int? {
    val resourceId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
    return if (resourceId == 0) null else resourceId
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

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

@Preview(showBackground = true)
@Composable
fun ExerciseOverlayPreview() {
    MicroMovementGuideTheme {
        ExerciseOverlay(appSettings = AppSettings(condition = "General Wellness"), onComplete = {})
    }
}
