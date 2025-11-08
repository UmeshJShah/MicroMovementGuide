package com.aivantage.micromovementguidetv

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object ExerciseDatabase {

    private var exerciseDefinitions: List<ExerciseDefinition> = emptyList()
    private var exercisePacks: List<ExercisePack> = emptyList()

    fun loadExercises(context: Context) {
        exerciseDefinitions = loadAsset(context, "exercise_definitions.json")
        exercisePacks = loadAsset(context, "exercise_packs.json")
    }

    private inline fun <reified T> loadAsset(context: Context, fileName: String): List<T> {
        return try {
            context.assets.open(fileName).bufferedReader().use {
                Gson().fromJson(it, object : TypeToken<List<T>>() {}.type)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getExercisePackForCondition(condition: String): ExercisePack? {
        return exercisePacks.find { it.condition == condition }
            ?: exercisePacks.find { it.condition == "General Wellness" }
    }

    fun getDefinitionById(id: String): ExerciseDefinition? {
        return exerciseDefinitions.find { it.id == id }
    }
}
