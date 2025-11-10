package com.aivantage.micromovementguidetv

import android.app.Application

class MicroMovementGuideApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ExerciseDatabase.loadExercises(this)
    }
}
