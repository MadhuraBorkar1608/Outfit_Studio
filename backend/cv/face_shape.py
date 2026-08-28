import math


def distance(point1, point2):
    """
    Calculate the distance between two facial landmark points.
    """

    return math.sqrt(
        (point1.x - point2.x) ** 2 +
        (point1.y - point2.y) ** 2
    )


def estimate_face_shape(landmarks):
    """
    Estimate face shape using approximate facial proportions.

    This is a proposed heuristic for the capstone project.
    It is not a scientifically exact classification method.
    """

    if not landmarks:
        return None

    # Approximate facial measurements using MediaPipe landmarks.
    #
    # Landmark indexes:
    # 10  = upper forehead area
    # 152 = lower chin area
    # 234 = left cheek area
    # 454 = right cheek area

    face_height = distance(landmarks[10], landmarks[152])
    face_width = distance(landmarks[234], landmarks[454])

    if face_width == 0:
        return None

    ratio = face_height / face_width

    # Proposed simple heuristic.
    if ratio >= 1.5:
        face_shape = "Oblong"
    elif ratio <= 1.05:
        face_shape = "Round"
    else:
        face_shape = "Oval"

    return {
        "face_shape": face_shape,
        "face_height_ratio": round(ratio, 2)
    }