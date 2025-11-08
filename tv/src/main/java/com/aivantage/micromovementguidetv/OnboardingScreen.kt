package com.aivantage.micromovementguidetv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: (AppSettings) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var tempSettings by remember { mutableStateOf(AppSettings()) }

    when (currentStep) {
        0 -> DisclaimerStep(onContinue = { currentStep = 1 })
        1 -> WelcomeStep(onContinue = { currentStep = 2 })
        2 -> ConditionStep(
            settings = tempSettings,
            onSettingsChanged = { tempSettings = it },
            onContinue = { currentStep = 3 }
        )
        3 -> AllSetStep(onOnboardingComplete = { onOnboardingComplete(tempSettings) })
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DisclaimerStep(onContinue: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Important: Safety First",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The exercises in this app are designed to be gentle. However, you should always listen to your body. Never stretch to the point of pain. Consult your doctor before starting any new exercise program.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Text("I Understand")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to the Micro-Movement Guide",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app runs quietly in the background. Every so often, it will gently pause what you're watching to guide you through a few simple movements.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Text("Continue")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ConditionStep(
    settings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onContinue: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Personalize Your Experience",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Select a focus area to get exercises tailored to you.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        PickerSettingItem(
            label = "Condition Focus",
            value = settings.condition,
            onValueChange = { direction ->
                val currentIndex = CONDITIONS.indexOf(settings.condition)
                val nextIndex = (currentIndex + direction + CONDITIONS.size) % CONDITIONS.size
                onSettingsChanged(settings.copy(condition = CONDITIONS[nextIndex]))
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Text("Continue")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AllSetStep(onOnboardingComplete: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You're All Set!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The guide is now active. Enjoy your show!\n\n(Hint: You can quickly pause the guide for the rest of the day by pressing the back button 5 times.)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onOnboardingComplete,
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Text("Finish Setup")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MicroMovementGuideTheme {
        OnboardingScreen(onOnboardingComplete = {})
    }
}
