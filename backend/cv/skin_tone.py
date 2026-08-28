import cv2
import numpy as np


def estimate_skin_tone(image, face):
    """
    Estimate skin tone from an approximate face region.

    This is a simple heuristic for the capstone prototype.
    The result is an estimate, not an exact skin-tone measurement.
    """

    # Face returned by the current face detector is expected
    # to contain x, y, width and height.
    x = int(face["x"])
    y = int(face["y"])
    w = int(face["width"])
    h = int(face["height"])

    # Select the central portion of the detected face.
    x1 = x + int(w * 0.25)
    x2 = x + int(w * 0.75)

    y1 = y + int(h * 0.25)
    y2 = y + int(h * 0.65)

    face_region = image[y1:y2, x1:x2]

    if face_region.size == 0:
        return None

    # Convert BGR to RGB.
    rgb_region = cv2.cvtColor(face_region, cv2.COLOR_BGR2RGB)

    # Calculate average RGB.
    average_rgb = np.mean(rgb_region, axis=(0, 1))

    brightness = np.mean(average_rgb)

    # Assumption: proposed prototype categories.
    if brightness < 100:
        tone = "Dark"
    elif brightness < 180:
        tone = "Medium"
    else:
        tone = "Light"

    return {
        "skin_tone": tone,
        "average_rgb": [
            round(float(average_rgb[0]), 2),
            round(float(average_rgb[1]), 2),
            round(float(average_rgb[2]), 2)
        ]
    }