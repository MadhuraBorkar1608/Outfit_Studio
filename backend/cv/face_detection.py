import cv2
import os


def detect_face(image):
    """
    Detects a face in an OpenCV image.

    Returns:
        Dictionary containing face coordinates if detected.
        None if no face is detected.
    """

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    cascade_path = os.path.join(
        cv2.data.haarcascades,
        "haarcascade_frontalface_default.xml"
    )

    if not os.path.exists(cascade_path):
        raise FileNotFoundError(
            f"Haar Cascade file not found: {cascade_path}"
        )

    face_cascade = cv2.CascadeClassifier(cascade_path)

    if face_cascade.empty():
        raise RuntimeError("Could not load Haar Cascade classifier")

    faces = face_cascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(80, 80)
    )

    if len(faces) == 0:
        return None

    x, y, width, height = faces[0]

    return {
        "x": int(x),
        "y": int(y),
        "width": int(width),
        "height": int(height)
    }