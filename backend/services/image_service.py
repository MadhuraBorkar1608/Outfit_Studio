from PIL import Image
from io import BytesIO


def validate_image(file):
    """
    Validates that the uploaded file is a readable image.
    """

    if file is None:
        return False, "No image provided"

    try:
        image = Image.open(file)
        image.verify()

        # Reset file position so it can be read again later
        file.seek(0)

        return True, "Image is valid"

    except Exception:
        return False, "Invalid image file"