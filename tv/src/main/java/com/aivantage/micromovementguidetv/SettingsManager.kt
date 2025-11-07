package com.aivantage.micromovementguidetv

import android.content.Context
import android.content.SharedPreferences

object SettingsManager {

    private const val PREFS_NAME = "MicroMovementPrefs"
    private const val KEY_HAS_ONBOARDED = "hasOnboarded"
    private const val KEY_BREAK_INTERVAL = "breakInterval"
    private const val KEY_EXERCISE_DURATION = "exerciseDuration"
    private const val KEY_CONDITION = "condition"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun hasOnboarded(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_HAS_ONBOARDED, false)
    }

    fun setHasOnboarded(context: Context, hasOnboarded: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_HAS_ONBOARDED, hasOnboarded).apply()
    }

    fun getSettings(context: Context): AppSettings {
        val prefs = getPrefs(context)
        return AppSettings(
            breakInterval = prefs.getInt(KEY_BREAK_INTERVAL, 20),
            exerciseDuration = prefs.getInt(KEY_EXERCISE_DURATION, 3),
            condition = prefs.getString(KEY_CONDITION, CONDITIONS.first()) ?: CONDITIONS.first()
        )
    }

    fun saveSettings(context: Context, settings: AppSettings) {
        val prefs = getPrefs(context)
        prefs.edit().apply {
            putInt(KEY_BREAK_INTERVAL, settings.breakInterval)
            putInt(KEY_EXERCISE_DURATION, settings.exerciseDuration)
            putString(KEY_CONDITION, settings.condition)
            apply()
        }
    }
}
