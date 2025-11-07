
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

# Stick figure proportions (based on 8-head rule, scaled for canvas)
HEAD_RADIUS = 20
NECK_LENGTH = 15
TORSO_LENGTH = 80
ARM_UPPER = 45
ARM_LOWER = 40
LEG_UPPER = 55
LEG_LOWER = 50

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

    # Create path for the line
    path = objects.Path()
    bezier = objects.Bezier()
    bezier.add_point([start_x, start_y], [0, 0], [0, 0])
    bezier.add_point([end_x, end_y], [0, 0], [0, 0])
    bezier.closed = False
    path.shape.value = bezier

    # Add stroke
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
    """Shoulder roll animation - circular rolling motion."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CANVAS_SIZE/2, CANVAS_SIZE/2]

    shoulder_group = objects.Group()
    add_circle_with_shadow(shoulder_group, 40, PRIMARY_COLOR)
    layer.add_shape(shoulder_group)

    # Circular rolling motion
    transform = shoulder_group.transform
    frames = FRAME_RATE * DURATION_SECONDS

    transform.position.add_keyframe(0, [0, 0])
    transform.position.add_keyframe(frames * 0.25, [0, -60])
    transform.position.add_keyframe(frames * 0.5, [60, -60])
    transform.position.add_keyframe(frames * 0.75, [60, 0])
    transform.position.add_keyframe(frames, [0, 0])

    return save_animation(animation, "shoulder_rolls.json")

def create_neck_stretch_animation():
    """Neck stretch animation - side to side motion."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CANVAS_SIZE/2, CANVAS_SIZE/2]

    # Head (larger circle)
    head_group = objects.Group()
    head_circle = objects.Ellipse()
    head_circle.size.value = [80, 100]
    head_fill = objects.Fill(color=PRIMARY_COLOR)
    head_group.add_shape(head_circle)
    head_group.add_shape(head_fill)
    layer.add_shape(head_group)

    # Animate rotation (tilt side to side)
    transform = head_group.transform
    frames = FRAME_RATE * DURATION_SECONDS

    transform.rotation.add_keyframe(0, 0)
    transform.rotation.add_keyframe(frames * 0.25, -15)
    transform.rotation.add_keyframe(frames * 0.5, 0)
    transform.rotation.add_keyframe(frames * 0.75, 15)
    transform.rotation.add_keyframe(frames, 0)

    return save_animation(animation, "neck_stretch.json")

def create_hand_clench_animation():
    """Hand clench animation - expanding and contracting fingers."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CANVAS_SIZE/2, CANVAS_SIZE/2]

    # Hand represented by 5 fingers (rectangles)
    for i in range(5):
        finger_group = objects.Group()
        finger_rect = objects.Rect()
        finger_rect.size.value = [12, 40]
        finger_fill = objects.Fill(color=PRIMARY_COLOR)
        finger_group.add_shape(finger_rect)
        finger_group.add_shape(finger_fill)

        # Position fingers in a fan shape
        angle = (i - 2) * 20  # -40, -20, 0, 20, 40 degrees
        x_offset = math.sin(math.radians(angle)) * 30
        finger_group.transform.position.value = [x_offset, 0]
        finger_group.transform.rotation.value = angle

        layer.add_shape(finger_group)

        # Animate scale (clench and release)
        frames = FRAME_RATE * DURATION_SECONDS
        transform = finger_group.transform
        transform.scale.add_keyframe(0, [100, 100])
        transform.scale.add_keyframe(frames * 0.4, [80, 60])  # Clenched
        transform.scale.add_keyframe(frames, [100, 100])      # Released

    return save_animation(animation, "hand_clenches.json")

def create_ankle_rotation_animation():
    """Ankle rotation animation - circular motion."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CANVAS_SIZE/2, CANVAS_SIZE/2]

    # Foot/ankle (ellipse)
    ankle_group = objects.Group()
    add_circle_with_shadow(ankle_group, 35, SECONDARY_COLOR)

    # Add arrow to show rotation direction
    arrow_group = objects.Group()
    arrow_rect = objects.Rect()
    arrow_rect.size.value = [8, 30]
    arrow_fill = objects.Fill(color=ACCENT_COLOR)
    arrow_group.add_shape(arrow_rect)
    arrow_group.add_shape(arrow_fill)
    arrow_group.transform.position.value = [0, -40]
    ankle_group.add_shape(arrow_group)

    layer.add_shape(ankle_group)

    # Rotate in a circle
    transform = ankle_group.transform
    frames = FRAME_RATE * DURATION_SECONDS

    transform.rotation.add_keyframe(0, 0)
    transform.rotation.add_keyframe(frames, 360)

    return save_animation(animation, "ankle_rotations.json")

def create_seated_marching_animation():
    """Seated marching animation - alternating knee lifts."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CANVAS_SIZE/2, CANVAS_SIZE/2]

    # Left leg
    left_leg_group = objects.Group()
    left_circle = objects.Ellipse()
    left_circle.size.value = [50, 70]
    left_fill = objects.Fill(color=PRIMARY_COLOR)
    left_leg_group.add_shape(left_circle)
    left_leg_group.add_shape(left_fill)
    left_leg_group.transform.position.value = [-40, 20]
    layer.add_shape(left_leg_group)

    # Right leg
    right_leg_group = objects.Group()
    right_circle = objects.Ellipse()
    right_circle.size.value = [50, 70]
    right_fill = objects.Fill(color=SECONDARY_COLOR)
    right_leg_group.add_shape(right_circle)
    right_leg_group.add_shape(right_fill)
    right_leg_group.transform.position.value = [40, 20]
    layer.add_shape(right_leg_group)

    frames = FRAME_RATE * DURATION_SECONDS

    # Animate left leg (up, down, up, down)
    left_leg_group.transform.position.add_keyframe(0, [-40, 20])
    left_leg_group.transform.position.add_keyframe(frames * 0.25, [-40, -10])
    left_leg_group.transform.position.add_keyframe(frames * 0.5, [-40, 20])
    left_leg_group.transform.position.add_keyframe(frames * 0.75, [-40, -10])
    left_leg_group.transform.position.add_keyframe(frames, [-40, 20])

    # Animate right leg (opposite timing)
    right_leg_group.transform.position.add_keyframe(0, [40, -10])
    right_leg_group.transform.position.add_keyframe(frames * 0.25, [40, 20])
    right_leg_group.transform.position.add_keyframe(frames * 0.5, [40, -10])
    right_leg_group.transform.position.add_keyframe(frames * 0.75, [40, 20])
    right_leg_group.transform.position.add_keyframe(frames, [40, -10])

    return save_animation(animation, "seated_marching.json")

def create_wrist_bend_animation():
    """Wrist bend animation - up and down motion."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CANVAS_SIZE/2, CANVAS_SIZE/2]

    # Forearm (rectangle)
    arm_group = objects.Group()
    arm_rect = objects.Rect()
    arm_rect.size.value = [30, 80]
    arm_fill = objects.Fill(color=SECONDARY_COLOR)
    arm_group.add_shape(arm_rect)
    arm_group.add_shape(arm_fill)
    arm_group.transform.position.value = [0, 30]
    layer.add_shape(arm_group)

    # Hand (rotates at wrist)
    hand_group = objects.Group()
    hand_rect = objects.Rect()
    hand_rect.size.value = [35, 50]
    hand_fill = objects.Fill(color=PRIMARY_COLOR)
    hand_group.add_shape(hand_rect)
    hand_group.add_shape(hand_fill)
    hand_group.transform.position.value = [0, -15]
    hand_group.transform.anchor_point.value = [0, 25]  # Pivot at wrist
    layer.add_shape(hand_group)

    # Animate wrist rotation
    frames = FRAME_RATE * DURATION_SECONDS
    hand_group.transform.rotation.add_keyframe(0, 0)
    hand_group.transform.rotation.add_keyframe(frames * 0.33, -30)
    hand_group.transform.rotation.add_keyframe(frames * 0.66, 30)
    hand_group.transform.rotation.add_keyframe(frames, 0)

    return save_animation(animation, "wrist_bends.json")

def create_deep_breathing_animation():
    """Deep breathing animation - expanding and contracting circle."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CANVAS_SIZE/2, CANVAS_SIZE/2]

    # Breathing circle (lungs representation)
    breath_group = objects.Group()
    breath_circle = objects.Ellipse()
    breath_circle.size.value = [80, 80]
    breath_fill = objects.Fill(color=PRIMARY_COLOR)
    breath_fill.opacity.value = 70  # Semi-transparent
    breath_group.add_shape(breath_circle)
    breath_group.add_shape(breath_fill)
    layer.add_shape(breath_group)

    # Animate scale (inhale and exhale)
    frames = FRAME_RATE * DURATION_SECONDS
    transform = breath_group.transform

    transform.scale.add_keyframe(0, [100, 100])           # Normal
    transform.scale.add_keyframe(frames * 0.4, [140, 140]) # Inhale
    transform.scale.add_keyframe(frames * 0.5, [140, 140]) # Hold
    transform.scale.add_keyframe(frames, [100, 100])       # Exhale

    # Animate opacity for breathing effect
    breath_fill.opacity.add_keyframe(0, 70)
    breath_fill.opacity.add_keyframe(frames * 0.4, 90)
    breath_fill.opacity.add_keyframe(frames, 70)

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

    print("=" * 50)
    print("Generating Lottie Animations")
    print("=" * 50)

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

    print("=" * 50)
    print(f"Finished! Generated {len(animations)} animations in {ASSET_DIR}")
    print("=" * 50)
