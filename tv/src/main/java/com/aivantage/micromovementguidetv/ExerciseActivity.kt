package com.aivantage.micromovementguidetv

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.aivantage.micromovementguidetv.ui.theme.MicroMovementGuideTheme

class ExerciseActivity : ComponentActivity() {

    private var exerciseService: ExerciseService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            AppLogger.log(this@ExerciseActivity, "ExerciseActivity connected to service.")
            val binder = service as ExerciseService.ExerciseServiceBinder
            exerciseService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            AppLogger.log(this@ExerciseActivity, "ExerciseActivity disconnected from service.")
            isBound = false
        }
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLogger.log(this, "ExerciseActivity created.")
        val appSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("appSettings", AppSettings::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("appSettings") as? AppSettings
        }


        setContent {
            MicroMovementGuideTheme(appSettings = appSettings ?: AppSettings()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (appSettings != null) {
                        ExerciseOverlay(
                            appSettings = appSettings,
                            onComplete = {
                                AppLogger.log(this@ExerciseActivity, "Exercise complete, abandoning audio focus and finishing activity.")
                                exerciseService?.abandonAudioFocus()
                                finish()
                            }
                        )
                    } else {
                        AppLogger.log(this@ExerciseActivity, "ExerciseActivity started with null appSettings, finishing.")
                        finish()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppLogger.log(this, "ExerciseActivity started, binding to service.")
        Intent(this, ExerciseService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        AppLogger.log(this, "ExerciseActivity stopped, unbinding from service.")
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
