
import os
import sys
import math
from lottie import objects
from lottie.exporters import export_lottie

# Helper to convert hex color to lottie's RGB format (values 0-1)
def hex_to_rgb(hex_color):
    h = hex_color.lstrip('#')
    return tuple(int(h[i:i+2], 16) / 255 for i in (0, 2, 4))

# --- Animation Configuration ---
ASSET_DIR = "tv/src/main/assets/"
FRAME_RATE = 60
DURATION_SECONDS = 3
CANVAS_SIZE = 512
CENTER = CANVAS_SIZE / 2

# --- Visual Style for Stick Figures ---
BODY_COLOR = objects.Color(*hex_to_rgb("#2C3E50"))        # Dark gray for body
HIGHLIGHT_COLOR = objects.Color(*hex_to_rgb("#E74C3C"))   # Red for active part
ACCENT_COLOR = objects.Color(*hex_to_rgb("#3498DB"))      # Blue for emphasis
LINE_WIDTH = 8
HIGHLIGHT_WIDTH = 12

# Stick figure proportions
HEAD_RADIUS = 20
NECK_Y = 30
SHOULDER_Y = 60
ELBOW_Y = 110
WRIST_Y = 155
HIP_Y = 145
KNEE_Y = 210
ANKLE_Y = 270
ARM_WIDTH = 25
LEG_WIDTH = 30

def create_base_animation():
    """Create a base animation with proper settings."""
    n_frames = DURATION_SECONDS * FRAME_RATE
    animation = objects.Animation(n_frames, FRAME_RATE)
    animation.width = CANVAS_SIZE
    animation.height = CANVAS_SIZE
    return animation

def create_line(start_x, start_y, end_x, end_y, color, width=LINE_WIDTH):
    """Create a line (limb) using Path."""
    group = objects.Group()
    path = objects.Path()
    bezier = objects.Bezier()
    bezier.add_point(objects.NVector(start_x, start_y))
    bezier.add_point(objects.NVector(end_x, end_y))
    bezier.closed = False
    path.shape.value = bezier

    stroke = objects.Stroke(color=color)
    stroke.width.value = width
    stroke.line_cap = 2  # Round cap

    group.add_shape(path)
    group.add_shape(stroke)
    return group

def create_head(x, y, radius=HEAD_RADIUS, color=BODY_COLOR):
    """Create a head (circle)."""
    group = objects.Group()
    circle = objects.Ellipse()
    circle.size.value = [radius * 2, radius * 2]
    circle.position.value = [x, y]

    stroke = objects.Stroke(color=color)
    stroke.width.value = LINE_WIDTH
    fill = objects.Fill(color=objects.Color(1, 1, 1))  # White fill

    group.add_shape(circle)
    group.add_shape(fill)
    group.add_shape(stroke)
    return group

def create_shoulder_roll_animation():
    """Stick figure performing shoulder rolls - focus on shoulders."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 100]

    # Static parts (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))  # Torso
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, KNEE_Y, BODY_COLOR))  # Left thigh
    layer.add_shape(create_line(-LEG_WIDTH, KNEE_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))  # Left shin
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, KNEE_Y, BODY_COLOR))  # Right thigh
    layer.add_shape(create_line(LEG_WIDTH, KNEE_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))  # Right shin

    # Animated shoulders (RED - highlighted)
    left_shoulder_group = objects.Group()
    left_arm = create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, ELBOW_Y, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    left_forearm = create_line(-ARM_WIDTH, ELBOW_Y, -ARM_WIDTH, WRIST_Y, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    left_shoulder_group.add_shape(left_arm)
    left_shoulder_group.add_shape(left_forearm)
    layer.add_shape(left_shoulder_group)

    right_shoulder_group = objects.Group()
    right_arm = create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, ELBOW_Y, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    right_forearm = create_line(ARM_WIDTH, ELBOW_Y, ARM_WIDTH, WRIST_Y, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    right_shoulder_group.add_shape(right_arm)
    right_shoulder_group.add_shape(right_forearm)
    layer.add_shape(right_shoulder_group)

    # Animate shoulder roll (circular motion)
    frames = FRAME_RATE * DURATION_SECONDS
    for shoulder_group in [left_shoulder_group, right_shoulder_group]:
        shoulder_group.transform.rotation.add_keyframe(0, 0)
        shoulder_group.transform.rotation.add_keyframe(frames * 0.25, -15)  # Up
        shoulder_group.transform.rotation.add_keyframe(frames * 0.5, -30)   # Back
        shoulder_group.transform.rotation.add_keyframe(frames * 0.75, -15)  # Down
        shoulder_group.transform.rotation.add_keyframe(frames, 0)           # Forward

    return save_animation(animation, "shoulder_rolls.json")

def create_neck_stretch_animation():
    """Stick figure with head tilting side to side - focus on neck."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 100]

    # Static body (gray)
    layer.add_shape(create_line(0, SHOULDER_Y, 0, HIP_Y, BODY_COLOR))  # Torso
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))  # Left arm
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, WRIST_Y, BODY_COLOR))  # Right arm
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))  # Left leg
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))  # Right leg

    # Animated head and neck (RED - highlighted)
    head_neck_group = objects.Group()
    neck = create_line(0, NECK_Y, 0, SHOULDER_Y, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    head = create_head(0, 0, HEAD_RADIUS, HIGHLIGHT_COLOR)
    head_neck_group.add_shape(neck)
    head_neck_group.add_shape(head)
    head_neck_group.transform.anchor_point.value = [0, SHOULDER_Y]
    head_neck_group.transform.position.value = [0, 0]
    layer.add_shape(head_neck_group)

    # Animate neck tilt
    frames = FRAME_RATE * DURATION_SECONDS
    head_neck_group.transform.rotation.add_keyframe(0, 0)
    head_neck_group.transform.rotation.add_keyframe(frames * 0.25, -20)  # Tilt left
    head_neck_group.transform.rotation.add_keyframe(frames * 0.5, 0)     # Center
    head_neck_group.transform.rotation.add_keyframe(frames * 0.75, 20)   # Tilt right
    head_neck_group.transform.rotation.add_keyframe(frames, 0)           # Center

    return save_animation(animation, "neck_stretch.json")

def create_hand_clench_animation():
    """Stick figure with hand opening/closing - focus on hand."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 100]

    # Body (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))

    # Arms - forearm in gray, hand highlighted
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, ELBOW_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, ELBOW_Y, BODY_COLOR))

    # Animated hands (RED - highlighted)
    # Left hand with fingers
    left_hand_group = objects.Group()
    left_hand_group.transform.position.value = [-ARM_WIDTH, ELBOW_Y]

    # Palm
    palm_length = 40
    left_hand_group.add_shape(create_line(0, 0, 0, palm_length, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH))

    # 5 fingers
    for i in range(5):
        finger_group = objects.Group()
        angle = (i - 2) * 15  # Fan shape
        finger_length = 15
        x_offset = math.sin(math.radians(angle)) * 5
        y_base = palm_length
        y_end = y_base + finger_length

        finger_line = create_line(x_offset, y_base, x_offset, y_end, HIGHLIGHT_COLOR, 4)
        finger_group.add_shape(finger_line)
        finger_group.transform.position.value = [0, 0]

        # Animate finger curl
        frames = FRAME_RATE * DURATION_SECONDS
        finger_group.transform.scale.add_keyframe(0, [100, 100])
        finger_group.transform.scale.add_keyframe(frames * 0.4, [100, 50])  # Clenched
        finger_group.transform.scale.add_keyframe(frames, [100, 100])       # Open

        left_hand_group.add_shape(finger_group)

    layer.add_shape(left_hand_group)

    # Right hand (mirror)
    right_hand_group = objects.Group()
    right_hand_group.transform.position.value = [ARM_WIDTH, ELBOW_Y]
    right_hand_group.add_shape(create_line(0, 0, 0, palm_length, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH))

    for i in range(5):
        finger_group = objects.Group()
        angle = (i - 2) * 15
        x_offset = math.sin(math.radians(angle)) * 5
        finger_line = create_line(x_offset, palm_length, x_offset, palm_length + 15, HIGHLIGHT_COLOR, 4)
        finger_group.add_shape(finger_line)

        frames = FRAME_RATE * DURATION_SECONDS
        finger_group.transform.scale.add_keyframe(0, [100, 100])
        finger_group.transform.scale.add_keyframe(frames * 0.4, [100, 50])
        finger_group.transform.scale.add_keyframe(frames, [100, 100])

        right_hand_group.add_shape(finger_group)

    layer.add_shape(right_hand_group)

    return save_animation(animation, "hand_clenches.json")

def create_ankle_rotation_animation():
    """Stick figure with one foot rotating - focus on ankle."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 50]

    # Upper body and left leg (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))  # Left leg

    # Right leg with highlighted ankle
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y - 30, BODY_COLOR))  # Right thigh/shin

    # Animated foot (RED - highlighted)
    foot_group = objects.Group()
    foot_group.transform.position.value = [LEG_WIDTH, ANKLE_Y - 30]

    # Foot shape (horizontal line)
    foot_line = create_line(-15, 0, 15, 0, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    foot_group.add_shape(foot_line)

    # Arrow indicator
    arrow_group = objects.Group()
    arrow = create_line(0, -20, 0, -35, ACCENT_COLOR, 6)
    arrow_group.add_shape(arrow)
    foot_group.add_shape(arrow_group)

    layer.add_shape(foot_group)

    # Animate rotation
    frames = FRAME_RATE * DURATION_SECONDS
    foot_group.transform.rotation.add_keyframe(0, 0)
    foot_group.transform.rotation.add_keyframe(frames, 360)

    return save_animation(animation, "ankle_rotations.json")

def create_seated_marching_animation():
    """Stick figure seated with legs marching - focus on legs."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 80]

    # Upper body (gray - static)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, WRIST_Y, BODY_COLOR))

    # Animated legs (RED - highlighted)
    # Left leg
    left_thigh_group = objects.Group()
    left_thigh = create_line(0, 0, 0, 60, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    left_shin = create_line(0, 60, 0, 120, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    left_thigh_group.add_shape(left_thigh)
    left_thigh_group.add_shape(left_shin)
    left_thigh_group.transform.position.value = [-LEG_WIDTH, HIP_Y]
    layer.add_shape(left_thigh_group)

    # Right leg
    right_thigh_group = objects.Group()
    right_thigh = create_line(0, 0, 0, 60, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    right_shin = create_line(0, 60, 0, 120, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    right_thigh_group.add_shape(right_thigh)
    right_thigh_group.add_shape(right_shin)
    right_thigh_group.transform.position.value = [LEG_WIDTH, HIP_Y]
    layer.add_shape(right_thigh_group)

    # Animate alternating knee lifts
    frames = FRAME_RATE * DURATION_SECONDS

    # Left leg
    left_thigh_group.transform.rotation.add_keyframe(0, 0)
    left_thigh_group.transform.rotation.add_keyframe(frames * 0.25, -30)  # Lift
    left_thigh_group.transform.rotation.add_keyframe(frames * 0.5, 0)     # Down
    left_thigh_group.transform.rotation.add_keyframe(frames * 0.75, -30)  # Lift
    left_thigh_group.transform.rotation.add_keyframe(frames, 0)           # Down

    # Right leg (opposite)
    right_thigh_group.transform.rotation.add_keyframe(0, -30)  # Start lifted
    right_thigh_group.transform.rotation.add_keyframe(frames * 0.25, 0)
    right_thigh_group.transform.rotation.add_keyframe(frames * 0.5, -30)
    right_thigh_group.transform.rotation.add_keyframe(frames * 0.75, 0)
    right_thigh_group.transform.rotation.add_keyframe(frames, -30)

    return save_animation(animation, "seated_marching.json")

def create_wrist_bend_animation():
    """Stick figure with wrist bending - focus on wrist."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 100]

    # Body and left arm (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))

    # Right arm - forearm gray, wrist/hand highlighted
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, ELBOW_Y + 30, BODY_COLOR))

    # Animated wrist and hand (RED - highlighted)
    wrist_group = objects.Group()
    wrist_group.transform.position.value = [ARM_WIDTH, ELBOW_Y + 30]
    wrist_group.transform.anchor_point.value = [0, 0]

    forearm_lower = create_line(0, 0, 0, 20, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    hand = create_line(0, 20, 0, 50, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    wrist_group.add_shape(forearm_lower)
    wrist_group.add_shape(hand)

    layer.add_shape(wrist_group)

    # Animate wrist flexion/extension
    frames = FRAME_RATE * DURATION_SECONDS
    wrist_group.transform.rotation.add_keyframe(0, 0)
    wrist_group.transform.rotation.add_keyframe(frames * 0.33, -40)  # Flex
    wrist_group.transform.rotation.add_keyframe(frames * 0.66, 40)   # Extend
    wrist_group.transform.rotation.add_keyframe(frames, 0)           # Neutral

    return save_animation(animation, "wrist_bends.json")

def create_deep_breathing_animation():
    """Stick figure with expanding chest - focus on breathing."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 100]

    # Head (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))

    # Arms and legs (gray)
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH - 10, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH + 10, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))

    # Animated torso/chest (RED - highlighted with breathing indicator)
    torso_group = objects.Group()
    torso = create_line(0, NECK_Y, 0, HIP_Y, HIGHLIGHT_COLOR, HIGHLIGHT_WIDTH)
    torso_group.add_shape(torso)

    # Breathing indicator (expanding circle on chest)
    breath_circle_group = objects.Group()
    breath_circle = objects.Ellipse()
    breath_circle.size.value = [30, 30]
    breath_circle.position.value = [0, 80]
    breath_fill = objects.Fill(color=ACCENT_COLOR)
    breath_fill.opacity.value = 50
    breath_circle_group.add_shape(breath_circle)
    breath_circle_group.add_shape(breath_fill)
    torso_group.add_shape(breath_circle_group)

    layer.add_shape(torso_group)

    # Animate breathing (chest expansion)
    frames = FRAME_RATE * DURATION_SECONDS

    # Scale breathing indicator
    breath_circle_group.transform.scale.add_keyframe(0, [100, 100])
    breath_circle_group.transform.scale.add_keyframe(frames * 0.4, [180, 180])  # Inhale
    breath_circle_group.transform.scale.add_keyframe(frames * 0.5, [180, 180])  # Hold
    breath_circle_group.transform.scale.add_keyframe(frames, [100, 100])        # Exhale

    # Opacity change
    breath_fill.opacity.add_keyframe(0, 50)
    breath_fill.opacity.add_keyframe(frames * 0.4, 80)
    breath_fill.opacity.add_keyframe(frames, 50)

    return save_animation(animation, "deep_breathing.json")

def save_animation(animation, filename):
    """Save animation to the assets folder."""
    if not os.path.exists(ASSET_DIR):
        os.makedirs(ASSET_DIR)

    output_path = os.path.join(ASSET_DIR, filename)
    with open(output_path, 'w') as f:
        export_lottie(animation, f, pretty=True)

    print(f"✓ Generated: {filename}")
    return output_path


if __name__ == "__main__":
    # Ensure lottie is installed
    try:
        from lottie import objects
    except ImportError:
        print("Error: The 'lottie' package is not installed for this Python interpreter.", file=sys.stderr)
        print(f"Please run: '{sys.executable} -m pip install lottie'", file=sys.stderr)
        sys.exit(1)

    print("=" * 60)
    print("Generating Stick Figure Lottie Animations")
    print("=" * 60)

    animations = [
        ("Shoulder Rolls", create_shoulder_roll_animation),
        ("Neck Stretch", create_neck_stretch_animation),
        ("Hand Clenches", create_hand_clench_animation),
        ("Ankle Rotations", create_ankle_rotation_animation),
        ("Seated Marching", create_seated_marching_animation),
        ("Wrist Bends", create_wrist_bend_animation),
        ("Deep Breathing", create_deep_breathing_animation),
    ]

    for name, func in animations:
        try:
            func()
        except Exception as e:
            print(f"✗ Error generating {name}: {e}")
            import traceback
            traceback.print_exc()

    print("=" * 60)
    print(f"Finished! Generated {len(animations)} stick figure animations")
    print("=" * 60)