import cv2

from cv.face_landmarks import detect_landmarks
from cv.face_shape import estimate_face_shape


image_path = input("Enter image path: ").strip()

image = cv2.imread(image_path)

if image is None:
    print("ERROR: Could not read image")
    exit()

landmarks = detect_landmarks(image)

if landmarks is None:
    print("ERROR: No facial landmarks detected")
    exit()

result = estimate_face_shape(landmarks)

if result is None:
    print("ERROR: Could not estimate face shape")
else:
    print("Estimated Face Shape:")
    print(result["face_shape"])
    print("Face Height Ratio:")
    print(result["face_height_ratio"])