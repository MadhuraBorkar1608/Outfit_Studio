from utils.database import get_db_connection


VALID_CATEGORIES = [
    "T-Shirts",
    "Shirts",
    "Jeans",
    "Trousers",
    "Footwear",
    "Jackets",
    "Accessories"
]


def add_wardrobe_item(
    user_id,
    name,
    category,
    color,
    image_path=None
):
    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor()

        # Check that the user exists
        cursor.execute(
            "SELECT id FROM users WHERE id = %s",
            (user_id,)
        )

        user = cursor.fetchone()

        if not user:
            return {
                "success": False,
                "message": "User not found"
            }

        # Validate category
        if category not in VALID_CATEGORIES:
            return {
                "success": False,
                "message": "Invalid category"
            }

        # Insert wardrobe item
        cursor.execute(
            """
            INSERT INTO wardrobe_items
            (
                user_id,
                name,
                category,
                color,
                image_path
            )
            VALUES (%s, %s, %s, %s, %s)
            """,
            (
                user_id,
                name,
                category,
                color,
                image_path
            )
        )

        connection.commit()

        item_id = cursor.lastrowid

        return {
            "success": True,
            "message": "Wardrobe item added successfully",
            "item_id": item_id
        }

    except Exception as e:

        if connection:
            connection.rollback()

        print("Wardrobe add error:", e)

        return {
            "success": False,
            "message": "Failed to add wardrobe item"
        }

    finally:

        if cursor:
            cursor.close()

        if connection:
            connection.close()


def get_wardrobe_items(user_id):

    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor(dictionary=True)

        # Check that the user exists
        cursor.execute(
            "SELECT id FROM users WHERE id = %s",
            (user_id,)
        )

        user = cursor.fetchone()

        if not user:
            return {
                "success": False,
                "message": "User not found"
            }

        cursor.execute(
            """
            SELECT
                id,
                user_id,
                name,
                category,
                color,
                image_path
            FROM wardrobe_items
            WHERE user_id = %s
            ORDER BY id DESC
            """,
            (user_id,)
        )

        items = cursor.fetchall()

        return {
            "success": True,
            "items": items
        }

    except Exception as e:

        print("Wardrobe retrieval error:", e)

        return {
            "success": False,
            "message": "Failed to retrieve wardrobe items"
        }

    finally:

        if cursor:
            cursor.close()

        if connection:
            connection.close()


def get_wardrobe_item(item_id):

    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor(dictionary=True)

        cursor.execute(
            """
            SELECT
                id,
                user_id,
                name,
                category,
                color,
                image_path
            FROM wardrobe_items
            WHERE id = %s
            """,
            (item_id,)
        )

        item = cursor.fetchone()

        if not item:
            return {
                "success": False,
                "message": "Wardrobe item not found"
            }

        return {
            "success": True,
            "item": item
        }

    except Exception as e:

        print("Wardrobe item retrieval error:", e)

        return {
            "success": False,
            "message": "Failed to retrieve wardrobe item"
        }

    finally:

        if cursor:
            cursor.close()

        if connection:
            connection.close()


def update_wardrobe_item(
    item_id,
    name,
    category,
    color
):
    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor()

        # Check whether item exists
        cursor.execute(
            """
            SELECT id
            FROM wardrobe_items
            WHERE id = %s
            """,
            (item_id,)
        )

        item = cursor.fetchone()

        if not item:
            return {
                "success": False,
                "message": "Wardrobe item not found"
            }

        # Validate category
        if category not in VALID_CATEGORIES:
            return {
                "success": False,
                "message": "Invalid category"
            }

        cursor.execute(
            """
            UPDATE wardrobe_items
            SET
                name = %s,
                category = %s,
                color = %s
            WHERE id = %s
            """,
            (
                name,
                category,
                color,
                item_id
            )
        )

        connection.commit()

        return {
            "success": True,
            "message": "Wardrobe item updated successfully"
        }

    except Exception as e:

        if connection:
            connection.rollback()

        print("Wardrobe update error:", e)

        return {
            "success": False,
            "message": "Failed to update wardrobe item"
        }

    finally:

        if cursor:
            cursor.close()

        if connection:
            connection.close()


def delete_wardrobe_item(item_id):

    connection = None
    cursor = None

    try:
        connection = get_db_connection()
        cursor = connection.cursor()

        # Check whether item exists
        cursor.execute(
            """
            SELECT id
            FROM wardrobe_items
            WHERE id = %s
            """,
            (item_id,)
        )

        item = cursor.fetchone()

        if not item:
            return {
                "success": False,
                "message": "Wardrobe item not found"
            }

        cursor.execute(
            """
            DELETE FROM wardrobe_items
            WHERE id = %s
            """,
            (item_id,)
        )

        connection.commit()

        return {
            "success": True,
            "message": "Wardrobe item deleted successfully"
        }

    except Exception as e:

        if connection:
            connection.rollback()

        print("Wardrobe delete error:", e)

        return {
            "success": False,
            "message": "Failed to delete wardrobe item"
        }

    finally:

        if cursor:
            cursor.close()

        if connection:
            connection.close()