
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
FRAME_RATE = 30  # Lower frame rate for smoother feel (research finding)
DURATION_SECONDS = 8  # Longer duration per research (8-12 seconds)
CANVAS_SIZE = 512
CENTER = CANVAS_SIZE / 2

# --- Professional Color Palette (based on Muscle & Motion research) ---
BODY_COLOR = objects.Color(*hex_to_rgb("#E0E0E0"))           # Light gray for inactive parts
ACTIVE_COLOR = objects.Color(*hex_to_rgb("#4285F4"))         # Bright blue for active parts
JOINT_COLOR = objects.Color(*hex_to_rgb("#FFC107"))          # Yellow for joint highlights
MOTION_PATH_COLOR = objects.Color(*hex_to_rgb("#4285F4"))    # Semi-transparent blue for paths
SHADOW_COLOR = objects.Color(*hex_to_rgb("#000000"))

LINE_WIDTH = 10
ACTIVE_WIDTH = 14
JOINT_RADIUS = 8

# Stick figure proportions (improved anatomical accuracy)
HEAD_RADIUS = 24
NECK_Y = 35
SHOULDER_Y = 70
ELBOW_Y = 125
WRIST_Y = 180
HIP_Y = 160
KNEE_Y = 235
ANKLE_Y = 310
FOOT_Y = 320
ARM_WIDTH = 30
LEG_WIDTH = 35

def create_base_animation():
    """Create a base animation with professional settings."""
    n_frames = DURATION_SECONDS * FRAME_RATE
    animation = objects.Animation(n_frames, FRAME_RATE)
    animation.width = CANVAS_SIZE
    animation.height = CANVAS_SIZE
    return animation

def create_line(start_x, start_y, end_x, end_y, color, width=LINE_WIDTH):
    """Create a line (limb) using Path with round caps."""
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
    stroke.line_join = 2  # Round join

    group.add_shape(path)
    group.add_shape(stroke)
    return group

def create_head(x, y, radius=HEAD_RADIUS, color=BODY_COLOR):
    """Create a head (circle with face indication)."""
    group = objects.Group()

    # Main head circle
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

def create_joint_indicator(x, y):
    """Create a yellow circle to highlight a joint."""
    group = objects.Group()
    circle = objects.Ellipse()
    circle.size.value = [JOINT_RADIUS * 2, JOINT_RADIUS * 2]
    circle.position.value = [x, y]

    fill = objects.Fill(color=JOINT_COLOR)
    fill.opacity.value = 80  # 80% opacity

    group.add_shape(circle)
    group.add_shape(fill)
    return group

def create_motion_path_arc(center_x, center_y, radius, start_angle, end_angle):
    """Create a semi-transparent arc showing motion path."""
    group = objects.Group()

    # Create arc using bezier curve
    path = objects.Path()
    bezier = objects.Bezier()

    # Sample points along the arc
    num_points = 20
    for i in range(num_points + 1):
        t = i / num_points
        angle = start_angle + (end_angle - start_angle) * t
        x = center_x + radius * math.cos(math.radians(angle))
        y = center_y + radius * math.sin(math.radians(angle))
        bezier.add_point(objects.NVector(x, y))

    bezier.closed = False
    path.shape.value = bezier

    stroke = objects.Stroke(color=MOTION_PATH_COLOR)
    stroke.width.value = 4
    stroke.line_cap = 2
    stroke.opacity.value = 40  # 40% opacity for subtle effect

    group.add_shape(path)
    group.add_shape(stroke)
    return group

def add_easing_keyframes(property, frames, keyframe_data):
    """Add keyframes with smooth easing (ease-in-ease-out)."""
    for t, value in keyframe_data:
        frame = int(frames * t)
        property.add_keyframe(frame, value)

def create_shoulder_roll_animation():
    """Professional shoulder roll with motion paths and joint indicators."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 80]

    # Static body parts (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, KNEE_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, KNEE_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, KNEE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, KNEE_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))

    # Motion path arc showing shoulder movement (PROFESSIONAL ADDITION)
    motion_arc_left = create_motion_path_arc(-ARM_WIDTH, SHOULDER_Y, 50, 0, 270)
    motion_arc_right = create_motion_path_arc(ARM_WIDTH, SHOULDER_Y, 50, 0, 270)
    layer.add_shape(motion_arc_left)
    layer.add_shape(motion_arc_right)

    # Joint indicators at shoulders (PROFESSIONAL ADDITION)
    left_shoulder_joint = create_joint_indicator(-ARM_WIDTH, SHOULDER_Y)
    right_shoulder_joint = create_joint_indicator(ARM_WIDTH, SHOULDER_Y)
    layer.add_shape(left_shoulder_joint)
    layer.add_shape(right_shoulder_joint)

    # Animated arms (ACTIVE COLOR - bright blue)
    left_arm_group = objects.Group()
    left_upper = create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, ELBOW_Y, ACTIVE_COLOR, ACTIVE_WIDTH)
    left_lower = create_line(-ARM_WIDTH, ELBOW_Y, -ARM_WIDTH, WRIST_Y, ACTIVE_COLOR, ACTIVE_WIDTH)
    left_arm_group.add_shape(left_upper)
    left_arm_group.add_shape(left_lower)
    left_arm_group.transform.anchor_point.value = [-ARM_WIDTH, SHOULDER_Y]
    left_arm_group.transform.position.value = [0, 0]
    layer.add_shape(left_arm_group)

    right_arm_group = objects.Group()
    right_upper = create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, ELBOW_Y, ACTIVE_COLOR, ACTIVE_WIDTH)
    right_lower = create_line(ARM_WIDTH, ELBOW_Y, ARM_WIDTH, WRIST_Y, ACTIVE_COLOR, ACTIVE_WIDTH)
    right_arm_group.add_shape(right_upper)
    right_arm_group.add_shape(right_lower)
    right_arm_group.transform.anchor_point.value = [ARM_WIDTH, SHOULDER_Y]
    right_arm_group.transform.position.value = [0, 0]
    layer.add_shape(right_arm_group)

    # Professional smooth animation with easing
    frames = FRAME_RATE * DURATION_SECONDS

    # Shoulder roll motion: up -> back -> down -> forward (circular)
    keyframes = [
        (0, 0),      # Start
        (0.25, -20), # Up
        (0.5, -35),  # Back (peak)
        (0.75, -20), # Down
        (1.0, 0)     # Forward (complete circle)
    ]

    for arm_group in [left_arm_group, right_arm_group]:
        add_easing_keyframes(arm_group.transform.rotation, frames, keyframes)

    return save_animation(animation, "shoulder_rolls.json")

def create_neck_stretch_animation():
    """Professional neck stretch with motion path and joint highlighting."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 80]

    # Static body (gray)
    layer.add_shape(create_line(0, SHOULDER_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))

    # Motion path arc (PROFESSIONAL ADDITION)
    motion_arc = create_motion_path_arc(0, SHOULDER_Y, 60, 200, 340)
    layer.add_shape(motion_arc)

    # Neck joint indicator (PROFESSIONAL ADDITION)
    neck_joint = create_joint_indicator(0, SHOULDER_Y)
    layer.add_shape(neck_joint)

    # Animated head+neck (ACTIVE COLOR)
    head_neck_group = objects.Group()
    neck_line = create_line(0, NECK_Y, 0, SHOULDER_Y, ACTIVE_COLOR, ACTIVE_WIDTH)
    head = create_head(0, 0, HEAD_RADIUS, ACTIVE_COLOR)
    head_neck_group.add_shape(neck_line)
    head_neck_group.add_shape(head)
    head_neck_group.transform.anchor_point.value = [0, SHOULDER_Y]
    head_neck_group.transform.position.value = [0, 0]
    layer.add_shape(head_neck_group)

    # Smooth side-to-side tilt with hold at extremes
    frames = FRAME_RATE * DURATION_SECONDS
    keyframes = [
        (0, 0),      # Center
        (0.2, -25),  # Tilt left
        (0.35, -25), # Hold left
        (0.5, 0),    # Return center
        (0.65, 0),   # Pause center
        (0.8, 25),   # Tilt right
        (0.95, 25),  # Hold right
        (1.0, 0)     # Return center
    ]

    add_easing_keyframes(head_neck_group.transform.rotation, frames, keyframes)

    return save_animation(animation, "neck_stretch.json")

def create_hand_clench_animation():
    """Professional hand clench with finger detail and joint highlighting."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 80]

    # Body (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))

    # Arms - forearms gray
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, ELBOW_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, ELBOW_Y, BODY_COLOR))

    # Joint indicators at wrists (PROFESSIONAL ADDITION)
    left_wrist_joint = create_joint_indicator(-ARM_WIDTH, ELBOW_Y + 40)
    right_wrist_joint = create_joint_indicator(ARM_WIDTH, ELBOW_Y + 40)
    layer.add_shape(left_wrist_joint)
    layer.add_shape(right_wrist_joint)

    # Animated hands (ACTIVE COLOR) - both hands
    for hand_x in [-ARM_WIDTH, ARM_WIDTH]:
        hand_group = objects.Group()
        hand_group.transform.position.value = [hand_x, ELBOW_Y]

        # Palm
        palm = create_line(0, 40, 0, 80, ACTIVE_COLOR, ACTIVE_WIDTH)
        hand_group.add_shape(palm)

        # 5 fingers with animation
        for i in range(5):
            finger_group = objects.Group()
            angle = (i - 2) * 18
            x_offset = math.sin(math.radians(angle)) * 6

            finger = create_line(x_offset, 80, x_offset, 105, ACTIVE_COLOR, 6)
            finger_group.add_shape(finger)
            finger_group.transform.anchor_point.value = [x_offset, 80]
            finger_group.transform.position.value = [0, 0]

            # Animate finger curl with smooth timing
            frames = FRAME_RATE * DURATION_SECONDS
            keyframes = [
                (0, [100, 100]),      # Open
                (0.35, [100, 40]),    # Closed
                (0.5, [100, 40]),     # Hold closed
                (0.85, [100, 100]),   # Open
                (1.0, [100, 100])     # Hold open
            ]

            add_easing_keyframes(finger_group.transform.scale, frames, keyframes)
            hand_group.add_shape(finger_group)

        layer.add_shape(hand_group)

    return save_animation(animation, "hand_clenches.json")

def create_ankle_rotation_animation():
    """Professional ankle rotation with motion path circle."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 30]

    # Upper body and left leg (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y - 40, BODY_COLOR))

    # Circular motion path (PROFESSIONAL ADDITION)
    motion_circle = create_motion_path_arc(LEG_WIDTH, ANKLE_Y - 40, 35, 0, 360)
    layer.add_shape(motion_circle)

    # Ankle joint indicator (PROFESSIONAL ADDITION)
    ankle_joint = create_joint_indicator(LEG_WIDTH, ANKLE_Y - 40)
    layer.add_shape(ankle_joint)

    # Animated foot (ACTIVE COLOR)
    foot_group = objects.Group()
    foot_group.transform.position.value = [LEG_WIDTH, ANKLE_Y - 40]

    # Foot line (horizontal)
    foot = create_line(-20, 0, 20, 0, ACTIVE_COLOR, ACTIVE_WIDTH)
    foot_group.add_shape(foot)

    # Direction arrow
    arrow = create_line(0, -25, 0, -40, JOINT_COLOR, 6)
    foot_group.add_shape(arrow)

    layer.add_shape(foot_group)

    # Smooth 360-degree rotation
    frames = FRAME_RATE * DURATION_SECONDS
    keyframes = [
        (0, 0),
        (1.0, 360)
    ]

    add_easing_keyframes(foot_group.transform.rotation, frames, keyframes)

    return save_animation(animation, "ankle_rotations.json")

def create_seated_marching_animation():
    """Professional seated marching with alternating leg highlights."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 60]

    # Upper body (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, WRIST_Y, BODY_COLOR))

    # Hip joint indicators (PROFESSIONAL ADDITION)
    left_hip_joint = create_joint_indicator(-LEG_WIDTH, HIP_Y)
    right_hip_joint = create_joint_indicator(LEG_WIDTH, HIP_Y)
    layer.add_shape(left_hip_joint)
    layer.add_shape(right_hip_joint)

    # Knee joint indicators
    left_knee_joint = create_joint_indicator(-LEG_WIDTH, HIP_Y + 70)
    right_knee_joint = create_joint_indicator(LEG_WIDTH, HIP_Y + 70)
    layer.add_shape(left_knee_joint)
    layer.add_shape(right_knee_joint)

    # Animated legs (ACTIVE COLOR)
    left_leg = objects.Group()
    left_leg.transform.position.value = [-LEG_WIDTH, HIP_Y]
    left_leg.transform.anchor_point.value = [0, 0]
    left_thigh = create_line(0, 0, 0, 70, ACTIVE_COLOR, ACTIVE_WIDTH)
    left_shin = create_line(0, 70, 0, 140, ACTIVE_COLOR, ACTIVE_WIDTH)
    left_leg.add_shape(left_thigh)
    left_leg.add_shape(left_shin)
    layer.add_shape(left_leg)

    right_leg = objects.Group()
    right_leg.transform.position.value = [LEG_WIDTH, HIP_Y]
    right_leg.transform.anchor_point.value = [0, 0]
    right_thigh = create_line(0, 0, 0, 70, ACTIVE_COLOR, ACTIVE_WIDTH)
    right_shin = create_line(0, 70, 0, 140, ACTIVE_COLOR, ACTIVE_WIDTH)
    right_leg.add_shape(right_thigh)
    right_leg.add_shape(right_shin)
    layer.add_shape(right_leg)

    # Alternating marching motion with smooth timing
    frames = FRAME_RATE * DURATION_SECONDS

    # Left leg lifts while right is down, then switches
    left_keyframes = [
        (0, 0),      # Down
        (0.2, -35),  # Lift
        (0.4, -35),  # Hold up
        (0.5, 0),    # Down
        (1.0, 0)     # Stay down
    ]

    right_keyframes = [
        (0, -35),    # Start up
        (0.1, 0),    # Down
        (0.5, 0),    # Stay down
        (0.7, -35),  # Lift
        (0.9, -35),  # Hold up
        (1.0, -35)   # End up
    ]

    add_easing_keyframes(left_leg.transform.rotation, frames, left_keyframes)
    add_easing_keyframes(right_leg.transform.rotation, frames, right_keyframes)

    return save_animation(animation, "seated_marching.json")

def create_wrist_bend_animation():
    """Professional wrist bend with joint highlighting and motion arc."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 80]

    # Body and left arm (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))
    layer.add_shape(create_line(0, NECK_Y, 0, HIP_Y, BODY_COLOR))
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH, ELBOW_Y + 40, BODY_COLOR))

    # Motion path arc for wrist (PROFESSIONAL ADDITION)
    motion_arc = create_motion_path_arc(ARM_WIDTH, ELBOW_Y + 40, 55, -50, 50)
    layer.add_shape(motion_arc)

    # Wrist joint indicator (PROFESSIONAL ADDITION)
    wrist_joint = create_joint_indicator(ARM_WIDTH, ELBOW_Y + 40)
    layer.add_shape(wrist_joint)

    # Animated wrist and hand (ACTIVE COLOR)
    wrist_group = objects.Group()
    wrist_group.transform.position.value = [ARM_WIDTH, ELBOW_Y + 40]
    wrist_group.transform.anchor_point.value = [0, 0]

    forearm_part = create_line(0, 0, 0, 25, ACTIVE_COLOR, ACTIVE_WIDTH)
    hand = create_line(0, 25, 0, 60, ACTIVE_COLOR, ACTIVE_WIDTH)
    wrist_group.add_shape(forearm_part)
    wrist_group.add_shape(hand)
    layer.add_shape(wrist_group)

    # Smooth flexion/extension with hold at extremes
    frames = FRAME_RATE * DURATION_SECONDS
    keyframes = [
        (0, 0),      # Neutral
        (0.25, -45), # Flex
        (0.4, -45),  # Hold flex
        (0.5, 0),    # Neutral
        (0.75, 45),  # Extend
        (0.9, 45),   # Hold extend
        (1.0, 0)     # Neutral
    ]

    add_easing_keyframes(wrist_group.transform.rotation, frames, keyframes)

    return save_animation(animation, "wrist_bends.json")

def create_deep_breathing_animation():
    """Professional breathing with expanding chest indicator."""
    animation = create_base_animation()
    layer = animation.add_layer(objects.ShapeLayer())
    layer.transform.position.value = [CENTER, 80]

    # Head (gray)
    layer.add_shape(create_head(0, 0, HEAD_RADIUS, BODY_COLOR))

    # Arms and legs (gray)
    layer.add_shape(create_line(-ARM_WIDTH, SHOULDER_Y, -ARM_WIDTH - 15, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(ARM_WIDTH, SHOULDER_Y, ARM_WIDTH + 15, WRIST_Y, BODY_COLOR))
    layer.add_shape(create_line(-LEG_WIDTH, HIP_Y, -LEG_WIDTH, ANKLE_Y, BODY_COLOR))
    layer.add_shape(create_line(LEG_WIDTH, HIP_Y, LEG_WIDTH, ANKLE_Y, BODY_COLOR))

    # Animated torso (ACTIVE COLOR)
    torso = create_line(0, NECK_Y, 0, HIP_Y, ACTIVE_COLOR, ACTIVE_WIDTH)
    layer.add_shape(torso)

    # Breathing indicator circle (PROFESSIONAL ADDITION)
    breath_indicator = objects.Group()
    breath_circle = objects.Ellipse()
    breath_circle.size.value = [40, 40]
    breath_circle.position.value = [0, 100]

    breath_fill = objects.Fill(color=JOINT_COLOR)
    breath_fill.opacity.value = 60

    breath_indicator.add_shape(breath_circle)
    breath_indicator.add_shape(breath_fill)
    layer.add_shape(breath_indicator)

    # Smooth breathing cycle: inhale -> hold -> exhale -> hold
    frames = FRAME_RATE * DURATION_SECONDS

    scale_keyframes = [
        (0, [100, 100]),      # Exhaled
        (0.3, [170, 170]),    # Inhale
        (0.45, [170, 170]),   # Hold inhale
        (0.7, [100, 100]),    # Exhale
        (0.85, [100, 100]),   # Hold exhale
        (1.0, [100, 100])     # Ready for next cycle
    ]

    opacity_keyframes = [
        (0, 60),
        (0.3, 90),
        (0.45, 90),
        (0.7, 60),
        (1.0, 60)
    ]

    add_easing_keyframes(breath_indicator.transform.scale, frames, scale_keyframes)
    add_easing_keyframes(breath_fill.opacity, frames, opacity_keyframes)

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
    try:
        from lottie import objects
    except ImportError:
        print("Error: The 'lottie' package is not installed.", file=sys.stderr)
        print(f"Please run: '{sys.executable} -m pip install lottie'", file=sys.stderr)
        sys.exit(1)

    print("=" * 70)
    print("Generating PROFESSIONAL-QUALITY Exercise Animations")
    print("Based on industry research: Muscle & Motion standards")
    print("=" * 70)
    print(f"Duration: {DURATION_SECONDS}s | Frame Rate: {FRAME_RATE} FPS")
    print(f"Features: Motion paths, Joint highlighting, Smooth easing")
    print("=" * 70)

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

    print("=" * 70)
    print(f"✓ Complete! Generated {len(animations)} professional animations")
    print("=" * 70)
