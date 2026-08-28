import cv2
from cv.face_detection import detect_face


image_path = input("Enter image path: ").strip()

image = cv2.imread(image_path)

if image is None:
    print("ERROR: Could not read image")
    exit()

face = detect_face(image)

if face is None:
    print("No face detected")
else:
    print("Face detected!")
    print(face)