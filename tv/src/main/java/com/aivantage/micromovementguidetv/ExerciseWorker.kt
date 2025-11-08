package com.aivantage.micromovementguidetv

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class ExerciseWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        AppLogger.log(applicationContext, "ExerciseWorker started.")
        // This worker's only job is to start the ExerciseService.
        // The service will then handle the audio focus and show the activity.
        val appSettings = SettingsManager.getSettings(applicationContext)
        val serviceIntent = Intent(applicationContext, ExerciseService::class.java).apply {
            putExtra("appSettings", appSettings)
        }
        applicationContext.startService(serviceIntent)
        AppLogger.log(applicationContext, "ExerciseService started from worker.")

        return Result.success()
    }
}
