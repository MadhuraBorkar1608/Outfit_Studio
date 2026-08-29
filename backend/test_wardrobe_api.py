import os
import requests


BASE_URL = "http://127.0.0.1:5000"

# CHANGE THIS to an existing user ID from your database
USER_ID = 1

# Change this if your test image has a different name
IMAGE_PATH = os.path.join(
    "test_data",
    "white_tshirt.jpg"
)


def print_result(title, response):
    print("\n" + "=" * 50)
    print(title)
    print("=" * 50)

    print("HTTP Status:", response.status_code)

    try:
        print("Response:")
        print(response.json())
    except Exception:
        print("Response:")
        print(response.text)


# --------------------------------------------------
# 1. TEST BACKEND
# --------------------------------------------------

print("\nTesting Flask backend...")

response = requests.get(
    f"{BASE_URL}/api/test"
)

print_result("BACKEND TEST", response)


if response.status_code != 200:
    print("\nBackend test failed.")
    print("Make sure Flask is running with: python app.py")
    exit()


# --------------------------------------------------
# 2. ADD WARDROBE ITEM
# --------------------------------------------------

print("\nAdding wardrobe item...")

if not os.path.exists(IMAGE_PATH):
    print("ERROR: Test image not found:")
    print(IMAGE_PATH)
    exit()


form_data = {
    "user_id": str(USER_ID),
    "name": "White T-Shirt",
    "category": "T-Shirts",
    "color": "White"
}

try:

    with open(IMAGE_PATH, "rb") as image_file:

        files = {
            "image": (
                os.path.basename(IMAGE_PATH),
                image_file,
                "image/jpeg"
            )
        }

        response = requests.post(
            f"{BASE_URL}/api/wardrobe",
            data=form_data,
            files=files
        )

except Exception as e:

    print("\nPOST request failed:")
    print(e)
    exit()


print_result("ADD WARDROBE ITEM", response)


if response.status_code != 201:
    print("\nWardrobe item creation failed.")
    exit()


post_result = response.json()

item_id = post_result.get("item_id")

if not item_id:
    print("\nNo item_id returned by API.")
    exit()


print("\nCreated item ID:", item_id)


# --------------------------------------------------
# 3. GET ALL WARDROBE ITEMS
# --------------------------------------------------

response = requests.get(
    f"{BASE_URL}/api/wardrobe/{USER_ID}"
)

print_result("GET ALL WARDROBE ITEMS", response)


# --------------------------------------------------
# 4. GET SINGLE ITEM
# --------------------------------------------------

response = requests.get(
    f"{BASE_URL}/api/wardrobe/item/{item_id}"
)

print_result("GET SINGLE WARDROBE ITEM", response)


# --------------------------------------------------
# 5. UPDATE ITEM
# --------------------------------------------------

update_data = {
    "name": "Updated White T-Shirt",
    "category": "T-Shirts",
    "color": "Blue"
}

response = requests.put(
    f"{BASE_URL}/api/wardrobe/{item_id}",
    json=update_data
)

print_result("UPDATE WARDROBE ITEM", response)


# --------------------------------------------------
# 6. GET ITEM AFTER UPDATE
# --------------------------------------------------

response = requests.get(
    f"{BASE_URL}/api/wardrobe/item/{item_id}"
)

print_result("GET ITEM AFTER UPDATE", response)


# --------------------------------------------------
# 7. DELETE ITEM
# --------------------------------------------------

response = requests.delete(
    f"{BASE_URL}/api/wardrobe/{item_id}"
)

print_result("DELETE WARDROBE ITEM", response)


# --------------------------------------------------
# 8. VERIFY DELETE
# --------------------------------------------------

response = requests.get(
    f"{BASE_URL}/api/wardrobe/item/{item_id}"
)

print_result("VERIFY DELETE", response)


print("\n" + "=" * 50)
print("WARDROBE API TEST COMPLETED")
print("=" * 50)