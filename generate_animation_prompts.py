
import json

# This script generates detailed prompts for a text-to-animation AI model
# (like Google Veo) to create the Lottie animations for our app.

def generate_prompts():
    """
    Generates and prints a list of detailed prompts for each exercise animation.
    """

    # This data is a direct reflection of the exercises in ExerciseDatabase.kt
    exercises = [
        {
            "name": "Neck Stretches",
            "description": "A person sitting upright on a simple chair. The head gently tilts to the left, holds for a moment, returns to the center, then gently tilts to the right, and holds. The movement should be slow and smooth.",
            "filename": "neck_stretch.json"
        },
        {
            "name": "Hand Clenches",
            "description": "A close-up of a hand. The hand starts open with fingers extended, then slowly closes into a gentle fist, holds for a moment, and then slowly opens back up to a fully extended position.",
            "filename": "hand_clenches.json"
        },
        {
            "name": "Ankle Rotations",
            "description": "A side view of a person sitting on a simple chair, with one leg extended slightly. The foot and ankle perform slow, deliberate clockwise rotations, followed by slow, deliberate counter-clockwise rotations.",
            "filename": "ankle_rotations.json"
        },
        {
            "name": "Shoulder Rolls",
            "description": "A person sitting upright on a simple chair. The shoulders slowly roll upwards towards the ears, then backwards, then down, in a smooth circular motion. The animation should show a few rotations in one direction, then reverse.",
            "filename": "shoulder_rolls.json"
        },
        {
            "name": "Seated Marching",
            "description": "A person sitting upright on a simple chair. They lift their left knee up towards their chest, then lower it. As the left foot touches the ground, the right knee begins to lift up. The motion should be like a slow, deliberate march while seated.",
            "filename": "seated_marching.json"
        },
        {
            "name": "Wrist Bends",
            "description": "A close-up of an arm extended forward. A second hand gently holds the wrist. The hand of the extended arm slowly bends downwards, holds, then slowly bends upwards, and holds. The supporting hand remains stationary.",
            "filename": "wrist_bends.json"
        },
        {
            "name": "Deep Breathing",
            "description": "A person sitting upright on a simple chair. A simple visual representation of lungs inside the chest cavity slowly expands as they inhale, and slowly contracts as they exhale. The shoulders should remain relaxed.",
            "filename": "deep_breathing.json"
        }
    ]

    # --- Prompt Configuration ---
    # This section defines the consistent style for all animations.
    base_prompt = (
        "Create a short, seamlessly looping 2D vector animation of a stylized, gender-neutral person. "
        "The style should be minimalist and clean, using thick, rounded lines. "
        "The color palette should be simple: a light grey figure (#E0E0E0) with one accent color (#4285F4) to highlight the moving body part. "
        "The background should be transparent. "
        "The output must be a Lottie JSON file."
    )

    print("--- Animation Prompts for AI Asset Generation ---")
    print("="*50)
    
    all_prompts = []
    for exercise in exercises:
        full_prompt = f"{base_prompt} The animation should clearly show: {exercise['description']}"
        prompt_data = {
            "exercise_name": exercise['name'],
            "suggested_filename": exercise['filename'],
            "generation_prompt": full_prompt
        }
        all_prompts.append(prompt_data)
        
        print(f"\n### Prompt for: {exercise['name']} ###\n")
        print(f"Suggested Filename: {exercise['filename']}\n")
        print(full_prompt)
        print("\n" + "-"*50)

    # For convenience, also save the prompts to a JSON file
    with open('animation_prompts.json', 'w') as f:
        json.dump(all_prompts, f, indent=2)
        
    print("\nSuccessfully generated all prompts.")
    print("A copy has also been saved to 'animation_prompts.json' in your project root.")


if __name__ == "__main__":
    generate_prompts()
