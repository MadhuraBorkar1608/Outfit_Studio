from flask import Blueprint, jsonify, request

from utils.database import get_db_connection


profile_bp = Blueprint("profile", __name__)


@profile_bp.route("/api/profile/<int:user_id>", methods=["GET"])
def get_profile(user_id):

    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor(dictionary=True)

        cursor.execute(
            """
            SELECT
                u.id,
                u.name,
                u.email,
                p.height,
                p.weight,
                p.chest,
                p.waist,
                p.hip,
                p.preferred_style,
                p.preferred_colors
            FROM users u
            LEFT JOIN user_profiles p
                ON u.id = p.user_id
            WHERE u.id = %s
            """,
            (user_id,)
        )

        profile = cursor.fetchone()

        if not profile:
            return jsonify({
                "success": False,
                "message": "User not found"
            }), 404

        return jsonify({
            "success": True,
            "profile": profile
        }), 200

    except Exception as e:
        print("Profile error:", e)

        return jsonify({
            "success": False,
            "message": "Failed to retrieve profile"
        }), 500

    finally:
        if cursor:
            cursor.close()

        if connection:
            connection.close()

@profile_bp.route("/api/profile/<int:user_id>", methods=["PUT"])
def update_profile(user_id):

    data = request.get_json()

    if not data:
        return jsonify({
            "success": False,
            "message": "Request body is required"
        }), 400

    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor()

        # Check whether user exists
        cursor.execute(
            "SELECT id FROM users WHERE id = %s",
            (user_id,)
        )

        user = cursor.fetchone()

        if not user:
            return jsonify({
                "success": False,
                "message": "User not found"
            }), 404

        # Get profile information
        height = data.get("height")
        weight = data.get("weight")
        chest = data.get("chest")
        waist = data.get("waist")
        hip = data.get("hip")
        preferred_style = data.get("preferred_style")
        preferred_colors = data.get("preferred_colors")

        # Check whether profile already exists
        cursor.execute(
            "SELECT id FROM user_profiles WHERE user_id = %s",
            (user_id,)
        )

        existing_profile = cursor.fetchone()

        if existing_profile:

            cursor.execute(
                """
                UPDATE user_profiles
                SET
                    height = %s,
                    weight = %s,
                    chest = %s,
                    waist = %s,
                    hip = %s,
                    preferred_style = %s,
                    preferred_colors = %s
                WHERE user_id = %s
                """,
                (
                    height,
                    weight,
                    chest,
                    waist,
                    hip,
                    preferred_style,
                    preferred_colors,
                    user_id
                )
            )

        else:

            cursor.execute(
                """
                INSERT INTO user_profiles
                (
                    user_id,
                    height,
                    weight,
                    chest,
                    waist,
                    hip,
                    preferred_style,
                    preferred_colors
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                """,
                (
                    user_id,
                    height,
                    weight,
                    chest,
                    waist,
                    hip,
                    preferred_style,
                    preferred_colors
                )
            )

        connection.commit()

        return jsonify({
            "success": True,
            "message": "Profile updated successfully"
        }), 200

    except Exception as e:

        if connection:
            connection.rollback()

        print("Profile update error:", e)

        return jsonify({
            "success": False,
            "message": "Failed to update profile"
        }), 500

    finally:

        if cursor:
            cursor.close()

        if connection:
            connection.close()