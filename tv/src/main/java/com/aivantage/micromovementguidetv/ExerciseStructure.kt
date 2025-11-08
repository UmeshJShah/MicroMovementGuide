package com.aivantage.micromovementguidetv

import java.io.Serializable

// Represents a unique, reusable exercise definition.
// In the future, this could be stored in a central database table.
data class ExerciseDefinition(
    val id: String, // Stable, unique ID like "neck_stretch_side_to_side"
    val name: String, // User-facing name like "Neck Stretches"
    val instruction: String, // Detailed instruction text
    val iconName: String, // The name of the drawable resource, e.g., "ic_neck_stretch"
    val lottieAnimationName: String? = null // Field for Lottie animations
)

// Represents one step in an exercise routine.
// It references a definition and sets a duration for this specific routine.
data class ExerciseStep(
    val exerciseId: String,
    val duration: Int // Duration in seconds for this step
) : Serializable

// Represents a complete exercise routine for a specific condition.
// This is what would be "unpacked" from a downloaded exercise pack.
data class ExercisePack(
    val condition: String, // Matches one of the CONDITIONS from AppSettings
    val steps: List<ExerciseStep>
) : Serializable

// Make AppSettings Serializable so it can be passed in an Intent
data class AppSettings(
    val breakInterval: Int = 20, // in minutes
    val exerciseDuration: Int = 3, // in minutes
    val condition: String = CONDITIONS.first(),
    val isHighContrast: Boolean = false,
    val preferredExerciseIds: List<String> = emptyList(),
    val dislikedExerciseIds: List<String> = emptyList()
) : Serializable

val CONDITIONS = listOf(
    "General Wellness",
    "Parkinson's",
    "Diabetes",
    "Arthritis",
    "Post-Surgery Recovery",
)
