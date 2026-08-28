from utils.database import get_db_connection


def save_appearance_analysis(
    user_id,
    face_shape,
    skin_tone,
    body_type
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

        # Check whether appearance analysis already exists
        cursor.execute(
            """
            SELECT id
            FROM appearance_analysis
            WHERE user_id = %s
            """,
            (user_id,)
        )

        existing = cursor.fetchone()

        if existing:
            # Update existing result
            cursor.execute(
                """
                UPDATE appearance_analysis
                SET
                    face_shape = %s,
                    skin_tone = %s,
                    body_type = %s
                WHERE user_id = %s
                """,
                (
                    face_shape,
                    skin_tone,
                    body_type,
                    user_id
                )
            )
        else:
            # Insert new result
            cursor.execute(
                """
                INSERT INTO appearance_analysis
                (
                    user_id,
                    face_shape,
                    skin_tone,
                    body_type
                )
                VALUES (%s, %s, %s, %s)
                """,
                (
                    user_id,
                    face_shape,
                    skin_tone,
                    body_type
                )
            )

        connection.commit()

        return {
            "success": True,
            "message": "Appearance analysis saved successfully"
        }

    except Exception as e:
        if connection:
            connection.rollback()

        print("Appearance storage error:", e)

        return {
            "success": False,
            "message": "Failed to save appearance analysis"
        }

    finally:
        if cursor:
            cursor.close()

        if connection:
            connection.close()