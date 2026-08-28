import cv2

from cv.face_detection import detect_face
from cv.skin_tone import estimate_skin_tone


image_path = input("Enter image path: ").strip()

image = cv2.imread(image_path)

if image is None:
    print("ERROR: Could not read image")
    exit()

face = detect_face(image)

if face is None:
    print("ERROR: No face detected")
    exit()

print("FACE DATA:", face)
print("FACE TYPE:", type(face))
result = estimate_skin_tone(image, face)

if result is None:
    print("ERROR: Could not estimate skin tone")
else:
    print("Estimated Skin Tone:")
    print(result["skin_tone"])

    print("Average RGB:")
    print(result["average_rgb"])