from flask import Blueprint, jsonify, request
import cv2
import numpy as np

from services.image_service import validate_image
from services.appearance_service import save_appearance_analysis

from cv.face_detection import detect_face
from cv.face_landmarks import detect_landmarks
from cv.face_shape import estimate_face_shape
from cv.skin_tone import estimate_skin_tone
from cv.body_type import estimate_body_type


appearance_bp = Blueprint(
    "appearance",
    __name__,
    url_prefix="/api/appearance"
)


@appearance_bp.route("/test", methods=["GET"])
def appearance_test():
    return jsonify({
        "success": True,
        "message": "Appearance Analysis API is working"
    }), 200


@appearance_bp.route("/analyze", methods=["POST"])
def analyze_appearance():

    # -----------------------------------
    # 1. Check user ID
    # -----------------------------------

    user_id = request.form.get("user_id")

    if not user_id:
        return jsonify({
            "success": False,
            "message": "User ID is required"
        }), 400

    try:
        user_id = int(user_id)
    except ValueError:
        return jsonify({
            "success": False,
            "message": "User ID must be an integer"
        }), 400


    # -----------------------------------
    # 2. Check image
    # -----------------------------------

    if "image" not in request.files:
        return jsonify({
            "success": False,
            "message": "Image is required"
        }), 400

    image_file = request.files["image"]

    is_valid, message = validate_image(image_file)

    if not is_valid:
        return jsonify({
            "success": False,
            "message": message
        }), 400


    # -----------------------------------
    # 3. Read image
    # -----------------------------------

    try:
        image_bytes = image_file.read()

        image_array = np.frombuffer(
            image_bytes,
            np.uint8
        )

        image = cv2.imdecode(
            image_array,
            cv2.IMREAD_COLOR
        )

        if image is None:
            return jsonify({
                "success": False,
                "message": "Invalid image"
            }), 400

    except Exception as e:
        print("Image processing error:", e)

        return jsonify({
            "success": False,
            "message": "Image could not be processed"
        }), 400


    # -----------------------------------
    # 4. Detect face
    # -----------------------------------

    try:
        face = detect_face(image)

        if face is None:
            return jsonify({
                "success": False,
                "message": "Face not detected"
            }), 400

    except Exception as e:
        print("Face detection error:", e)

        return jsonify({
            "success": False,
            "message": "Face detection failed"
        }), 500


    # -----------------------------------
    # 5. Detect facial landmarks
    # -----------------------------------

    try:
        landmarks = detect_landmarks(image)

        if landmarks is None:
            return jsonify({
                "success": False,
                "message": "Facial landmarks could not be detected"
            }), 400

    except Exception as e:
        print("Landmark detection error:", e)

        return jsonify({
            "success": False,
            "message": "Facial landmark detection failed"
        }), 500


    # -----------------------------------
    # 6. Estimate face shape
    # -----------------------------------

    try:
        face_shape_result = estimate_face_shape(landmarks)

        if face_shape_result is None:
            return jsonify({
                "success": False,
                "message": "Face shape could not be estimated"
            }), 400

        face_shape = face_shape_result["face_shape"]

    except Exception as e:
        print("Face shape error:", e)

        return jsonify({
            "success": False,
            "message": "Face shape estimation failed"
        }), 500


    # -----------------------------------
    # 7. Estimate skin tone
    # -----------------------------------

    try:
        skin_tone_result = estimate_skin_tone(
            image,
            face
        )

        if skin_tone_result is None:
            return jsonify({
                "success": False,
                "message": "Skin tone could not be estimated"
            }), 400

        skin_tone = skin_tone_result["skin_tone"]

    except Exception as e:
        print("Skin tone error:", e)

        return jsonify({
            "success": False,
            "message": "Skin tone estimation failed"
        }), 500


    # -----------------------------------
    # 8. Get body measurements
    # -----------------------------------

    chest = request.form.get("chest")
    waist = request.form.get("waist")
    hip = request.form.get("hip")

    if chest is None or waist is None or hip is None:
        return jsonify({
            "success": False,
            "message": "Chest, waist and hip measurements are required"
        }), 400


    # -----------------------------------
    # 9. Estimate body type
    # -----------------------------------

    try:
        body_type_result = estimate_body_type(
            chest,
            waist,
            hip
        )

        if not body_type_result.get("success"):
            return jsonify(body_type_result), 400

        body_type = body_type_result["body_type"]

    except Exception as e:
        print("Body type error:", e)

        return jsonify({
            "success": False,
            "message": "Body type estimation failed"
        }), 500


    # -----------------------------------
    # 10. Save results
    # -----------------------------------

    storage_result = save_appearance_analysis(
        user_id=user_id,
        face_shape=face_shape,
        skin_tone=skin_tone,
        body_type=body_type
    )

    if not storage_result.get("success"):
        status_code = 404 if storage_result.get("message") == "User not found" else 500

        return jsonify(storage_result), status_code


    # -----------------------------------
    # 11. Final response
    # -----------------------------------

    return jsonify({
        "success": True,
        "appearance": {
            "face_shape": face_shape,
            "skin_tone": skin_tone,
            "body_type": body_type
        }
    }), 200