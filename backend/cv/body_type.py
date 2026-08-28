def estimate_body_type(chest, waist, hip):
    """
    Estimate body type using approximate body measurements.

    This is a proposed rule-based heuristic for the
    Outfit Studio capstone prototype.
    """

    if chest is None or waist is None or hip is None:
        return {
            "success": False,
            "message": "Chest, waist and hip measurements are required"
        }

    chest = float(chest)
    waist = float(waist)
    hip = float(hip)

    if chest <= 0 or waist <= 0 or hip <= 0:
        return {
            "success": False,
            "message": "Measurements must be greater than zero"
        }

    # Proposed heuristic.
    difference = abs(chest - hip)

    if difference <= 5 and waist < chest * 0.80:
        body_type = "Hourglass"

    elif hip > chest * 1.05:
        body_type = "Triangle"

    elif chest > hip * 1.05:
        body_type = "Inverted Triangle"

    else:
        body_type = "Rectangle"

    return {
        "success": True,
        "body_type": body_type
    }