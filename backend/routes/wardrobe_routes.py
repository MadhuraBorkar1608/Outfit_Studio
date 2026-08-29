from flask import Blueprint, jsonify, request
from pathlib import Path
from uuid import uuid4

from services.image_service import validate_image
from services.wardrobe_service import (
    add_wardrobe_item,
    get_wardrobe_items,
    get_wardrobe_item,
    update_wardrobe_item,
    delete_wardrobe_item
)


wardrobe_bp = Blueprint(
    "wardrobe",
    __name__,
    url_prefix="/api/wardrobe"
)


UPLOAD_FOLDER = Path(__file__).resolve().parent.parent / "uploads" / "wardrobe"


ALLOWED_CATEGORIES = [
    "T-Shirts",
    "Shirts",
    "Jeans",
    "Trousers",
    "Footwear",
    "Jackets",
    "Accessories"
]


@wardrobe_bp.route("", methods=["POST"])
def add_item():

    user_id = request.form.get("user_id")
    name = request.form.get("name")
    category = request.form.get("category")
    color = request.form.get("color")

    # Validate required fields
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

    if not name:
        return jsonify({
            "success": False,
            "message": "Name is required"
        }), 400

    if not category:
        return jsonify({
            "success": False,
            "message": "Category is required"
        }), 400

    if category not in ALLOWED_CATEGORIES:
        return jsonify({
            "success": False,
            "message": "Invalid category"
        }), 400

    if not color:
        return jsonify({
            "success": False,
            "message": "Color is required"
        }), 400

    # Check image
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

    # Create upload directory if necessary
    UPLOAD_FOLDER.mkdir(
        parents=True,
        exist_ok=True
    )

    # Generate safe unique filename
    original_extension = Path(
        image_file.filename or ""
    ).suffix.lower()

    if not original_extension:
        original_extension = ".jpg"

    filename = f"{uuid4().hex}{original_extension}"

    image_path = UPLOAD_FOLDER / filename

    # Save image
    try:
        image_file.save(image_path)

    except Exception as e:

        print("Wardrobe image save error:", e)

        return jsonify({
            "success": False,
            "message": "Failed to save image"
        }), 500

    # Store relative path in database
    relative_image_path = f"uploads/wardrobe/{filename}"

    result = add_wardrobe_item(
        user_id=user_id,
        name=name,
        category=category,
        color=color,
        image_path=relative_image_path
    )

    if not result.get("success"):

        # Remove image if database operation fails
        try:
            if image_path.exists():
                image_path.unlink()
        except Exception as e:
            print("Temporary image cleanup error:", e)

        status_code = (
            404
            if result.get("message") == "User not found"
            else 400
        )

        return jsonify(result), status_code

    return jsonify(result), 201


@wardrobe_bp.route("/<int:user_id>", methods=["GET"])
def get_items(user_id):

    result = get_wardrobe_items(user_id)

    if not result.get("success"):
        status_code = (
            404
            if result.get("message") == "User not found"
            else 500
        )

        return jsonify(result), status_code

    return jsonify(result), 200


@wardrobe_bp.route("/item/<int:item_id>", methods=["GET"])
def get_item(item_id):

    result = get_wardrobe_item(item_id)

    if not result.get("success"):
        return jsonify(result), 404

    return jsonify(result), 200


@wardrobe_bp.route("/<int:item_id>", methods=["PUT"])
def update_item(item_id):

    data = request.get_json()

    if not data:
        return jsonify({
            "success": False,
            "message": "Request body is required"
        }), 400

    name = data.get("name")
    category = data.get("category")
    color = data.get("color")

    if not name:
        return jsonify({
            "success": False,
            "message": "Name is required"
        }), 400

    if not category:
        return jsonify({
            "success": False,
            "message": "Category is required"
        }), 400

    if category not in ALLOWED_CATEGORIES:
        return jsonify({
            "success": False,
            "message": "Invalid category"
        }), 400

    if not color:
        return jsonify({
            "success": False,
            "message": "Color is required"
        }), 400

    result = update_wardrobe_item(
        item_id=item_id,
        name=name,
        category=category,
        color=color
    )

    if not result.get("success"):
        status_code = (
            404
            if result.get("message") == "Wardrobe item not found"
            else 500
        )

        return jsonify(result), status_code

    return jsonify(result), 200


@wardrobe_bp.route("/<int:item_id>", methods=["DELETE"])
def delete_item(item_id):

    result = delete_wardrobe_item(item_id)

    if not result.get("success"):
        return jsonify(result), 404

    return jsonify(result), 200