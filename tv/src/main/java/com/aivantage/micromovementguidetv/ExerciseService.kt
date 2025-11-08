package com.aivantage.micromovementguidetv

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExerciseService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var appSettings: AppSettings? = null
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private val binder = ExerciseServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        AppLogger.log(this, "ExerciseService bound.")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        AppLogger.log(this, "ExerciseService created.")
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        AppLogger.log(this, "ExerciseService onStartCommand.")
        if (intent?.hasExtra("appSettings") == true) {
            appSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("appSettings", AppSettings::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("appSettings") as? AppSettings
            }
            AppLogger.log(this, "Settings loaded from intent.")
        } else if (appSettings == null) {
            // Service was restarted by the system, reload settings
            appSettings = SettingsManager.getSettings(this)
            AppLogger.log(this, "Settings loaded from SettingsManager.")
        }

        serviceScope.launch {
            AppLogger.log(this@ExerciseService, "Requesting audio focus.")
            if (requestAudioFocus()) {
                AppLogger.log(this@ExerciseService, "Audio focus granted, starting ExerciseActivity.")
                val exerciseIntent = Intent(this@ExerciseService, ExerciseActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("appSettings", appSettings)
                }
                startActivity(exerciseIntent)
            } else {
                AppLogger.log(this@ExerciseService, "Audio focus denied.")
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun requestAudioFocus(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { }
                .build()

            val result = audioManager.requestAudioFocus(audioFocusRequest!!)
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    fun abandonAudioFocus() {
        AppLogger.log(this, "Abandoning audio focus.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(it)
                audioFocusRequest = null
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
        // Stop the service after the exercise is complete
        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        AppLogger.log(this, "ExerciseService destroyed.")
        abandonAudioFocus()
        serviceJob.cancel()
    }

    inner class ExerciseServiceBinder : Binder() {
        fun getService(): ExerciseService = this@ExerciseService
    }
}
