import cv2
import mediapipe as mp


def detect_landmarks(image):
    """
    Detect facial landmarks using MediaPipe Face Landmarker.
    """

    rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

    BaseOptions = mp.tasks.BaseOptions
    FaceLandmarker = mp.tasks.vision.FaceLandmarker
    FaceLandmarkerOptions = mp.tasks.vision.FaceLandmarkerOptions
    VisionRunningMode = mp.tasks.vision.RunningMode

    options = FaceLandmarkerOptions(
        base_options=BaseOptions(
            model_asset_path="face_landmarker.task"
        ),
        running_mode=VisionRunningMode.IMAGE,
        num_faces=1
    )

    mp_image = mp.Image(
        image_format=mp.ImageFormat.SRGB,
        data=rgb_image
    )

    with FaceLandmarker.create_from_options(options) as landmarker:
        result = landmarker.detect(mp_image)

    if not result.face_landmarks:
        return None

    return result.face_landmarks[0]