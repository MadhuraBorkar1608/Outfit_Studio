import cv2
from cv.face_landmarks import detect_landmarks


image_path = input("Enter image path: ").strip()

image = cv2.imread(image_path)

if image is None:
    print("ERROR: Could not read image")
    exit()

landmarks = detect_landmarks(image)

if landmarks is None:
    print("No facial landmarks detected")
else:
    print("Facial landmarks detected!")
    print("Number of landmarks:", len(landmarks))