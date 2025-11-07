package com.aivantage.micromovementguidetv

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.view.KeyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExerciseService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var appSettings: AppSettings? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        appSettings = intent?.getSerializableExtra("appSettings") as? AppSettings

        serviceScope.launch {
            while (true) {
                val breakInterval = appSettings?.breakInterval ?: 20
                delay(breakInterval * 60 * 1000L)
                pauseYouTube()
                val exerciseIntent = Intent(this@ExerciseService, ExerciseActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("appSettings", appSettings)
                }
                startActivity(exerciseIntent)
            }
        }
        return START_STICKY
    }

    private fun pauseYouTube() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE)
        audioManager.dispatchMediaKeyEvent(keyEvent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
