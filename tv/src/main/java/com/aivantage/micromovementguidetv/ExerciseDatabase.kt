package com.aivantage.micromovementguidetv

object ExerciseDatabase {

    // Master list of all possible exercises in the app.
    private val exerciseDefinitions = listOf(
        ExerciseDefinition(
            id = "neck_stretch_side_to_side",
            name = "Neck Stretches",
            instruction = "Gently tilt your head from side to side, holding each stretch for a few seconds.",
            iconName = "ic_neck_stretch",
            animationName = "neck_stretch.json" // Future-proofing
        ),
        ExerciseDefinition(
            id = "hand_clenches",
            name = "Hand Clenches",
            instruction = "Slowly open and close your hands, stretching your fingers wide and then making a gentle fist.",
            iconName = "ic_hand_clench",
            animationName = "hand_clenches.json"
        ),
        ExerciseDefinition(
            id = "ankle_rotations",
            name = "Ankle Rotations",
            instruction = "Lift one foot slightly and gently rotate your ankle in circles, first one way, then the other. Repeat with the other foot.",
            iconName = "ic_ankle_rotation",
            animationName = "ankle_rotations.json"
        ),
        ExerciseDefinition(
            id = "shoulder_rolls",
            name = "Shoulder Rolls",
            instruction = "Gently roll your shoulders upwards, backwards, and down. Then reverse the direction.",
            iconName = "ic_shoulder_roll",
            animationName = "shoulder_rolls.json"
        ),
        ExerciseDefinition(
            id = "seated_marching",
            name = "Seated Marching",
            instruction = "While seated, lift your knees one at a time as if you are marching.",
            iconName = "ic_seated_marching",
            animationName = "seated_marching.json"
        ),
        ExerciseDefinition(
            id = "wrist_bends",
            name = "Wrist Bends",
            instruction = "Extend one arm and gently bend your wrist up and down with your other hand.",
            iconName = "ic_wrist_bends",
            animationName = "wrist_bends.json"
        ),
        ExerciseDefinition(
            id = "deep_breathing",
            name = "Deep Breathing",
            instruction = "Breathe in slowly and deeply through your nose, and then exhale slowly through your mouth.",
            iconName = "ic_deep_breathing",
            animationName = "deep_breathing.json"
        )
    )

    private val exercisePacks = listOf(
        ExercisePack(
            condition = "General Wellness",
            steps = listOf(
                ExerciseStep("deep_breathing", 45),
                ExerciseStep("seated_marching", 90),
                ExerciseStep("shoulder_rolls", 45)
            )
        ),
        ExercisePack(
            condition = "Parkinson's",
            steps = listOf(
                ExerciseStep("hand_clenches", 60),
                ExerciseStep("wrist_bends", 60),
                ExerciseStep("seated_marching", 60)
            )
        ),
        ExercisePack(
            condition = "Diabetes",
            steps = listOf(
                ExerciseStep("ankle_rotations", 60),
                ExerciseStep("seated_marching", 90),
                ExerciseStep("hand_clenches", 30)
            )
        ),
        ExercisePack(
            condition = "Arthritis",
            steps = listOf(
                ExerciseStep("wrist_bends", 60),
                ExerciseStep("shoulder_rolls", 60),
                ExerciseStep("neck_stretch_side_to_side", 60)
            )
        ),
        ExercisePack(
            condition = "Post-Surgery Recovery",
            steps = listOf(
                ExerciseStep("deep_breathing", 90),
                ExerciseStep("ankle_rotations", 90)
            )
        )
    )

    fun getExercisePackForCondition(condition: String): ExercisePack? {
        return exercisePacks.find { it.condition == condition } 
               ?: exercisePacks.find { it.condition == "General Wellness" }
    }

    fun getDefinitionById(id: String): ExerciseDefinition? {
        return exerciseDefinitions.find { it.id == id }
    }
}
