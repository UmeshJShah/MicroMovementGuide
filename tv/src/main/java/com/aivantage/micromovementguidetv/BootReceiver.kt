package com.aivantage.micromovementguidetv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AppLogger.log(context, "Boot completed, checking onboarding status.")
            // Only start the worker if the user has completed onboarding
            if (SettingsManager.hasOnboarded(context)) {
                AppLogger.log(context, "User has onboarded, scheduling worker.")
                val appSettings = SettingsManager.getSettings(context)
                scheduleExerciseWorker(context, appSettings)
            } else {
                AppLogger.log(context, "User has not onboarded, not scheduling worker.")
            }
        }
    }

    private fun scheduleExerciseWorker(context: Context, appSettings: AppSettings) {
        AppLogger.log(context, "Scheduling ExerciseWorker for every ${appSettings.breakInterval} minutes.")
        val workManager = WorkManager.getInstance(context)
        val workRequest = PeriodicWorkRequestBuilder<ExerciseWorker>(
            appSettings.breakInterval.toLong(),
            TimeUnit.MINUTES
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "exerciseWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
