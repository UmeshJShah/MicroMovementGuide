package com.aivantage.micromovementguidetv

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme

class ExerciseActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appSettings = intent.getSerializableExtra("appSettings") as? AppSettings

        setContent {
            MicroMovementGuideTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (appSettings != null) {
                        ExerciseOverlay(
                            appSettings = appSettings,
                            onComplete = {
                                resumeYouTube()
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun resumeYouTube() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY)
        audioManager.dispatchMediaKeyEvent(keyEvent)
    }
}
