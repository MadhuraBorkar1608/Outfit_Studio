from flask import Blueprint, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash

from utils.database import get_db_connection


auth_bp = Blueprint("auth", __name__)


@auth_bp.route("/api/register", methods=["POST"])
def register():

    # Get JSON data from the request
    data = request.get_json()

    # Check whether request contains JSON
    if not data:
        return jsonify({
            "success": False,
            "message": "Request body is required"
        }), 400

    # Get required fields
    name = data.get("name")
    email = data.get("email")
    password = data.get("password")

    # Validate required fields
    if not name or not email or not password:
        return jsonify({
            "success": False,
            "message": "Name, email and password are required"
        }), 400

    # Connect to MySQL
    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor(dictionary=True)

        # Check whether email already exists
        cursor.execute(
            "SELECT id FROM users WHERE email = %s",
            (email,)
        )

        existing_user = cursor.fetchone()

        if existing_user:
            return jsonify({
                "success": False,
                "message": "Email already exists"
            }), 409

        # Hash password before storing it
        password_hash = generate_password_hash(password)

        # Insert new user
        cursor.execute(
            """
            INSERT INTO users (name, email, password)
            VALUES (%s, %s, %s)
            """,
            (name, email, password_hash)
        )

        connection.commit()

        user_id = cursor.lastrowid

        return jsonify({
            "success": True,
            "message": "Registration successful",
            "user_id": user_id
        }), 201

    except Exception as e:

        if connection:
            connection.rollback()

        return jsonify({
            "success": False,
            "message": "Registration failed"
        }), 500

    finally:

        if cursor:
            cursor.close()

        if connection:
            connection.close()

    from werkzeug.security import check_password_hash


@auth_bp.route("/api/login", methods=["POST"])
def login():

    data = request.get_json()

    if not data:
        return jsonify({
            "success": False,
            "message": "Request body is required"
        }), 400

    email = data.get("email")
    password = data.get("password")

    if not email or not password:
        return jsonify({
            "success": False,
            "message": "Email and password are required"
        }), 400

    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor(dictionary=True)

        cursor.execute(
            "SELECT id, name, email, password FROM users WHERE email = %s",
            (email,)
        )

        user = cursor.fetchone()

        if not user:
            return jsonify({
                "success": False,
                "message": "Invalid email or password"
            }), 401

        if not check_password_hash(user["password"], password):
            return jsonify({
                "success": False,
                "message": "Invalid email or password"
            }), 401

        return jsonify({
            "success": True,
            "message": "Login successful",
            "user_id": user["id"],
            "name": user["name"],
            "email": user["email"]
        }), 200

    except Exception:
        return jsonify({
            "success": False,
            "message": "Login failed"
        }), 500

    finally:
        if cursor:
            cursor.close()

        if connection:
            connection.close()