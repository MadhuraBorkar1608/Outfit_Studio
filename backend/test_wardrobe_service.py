from services.wardrobe_service import (
    add_wardrobe_item,
    get_wardrobe_items,
    get_wardrobe_item,
    update_wardrobe_item,
    delete_wardrobe_item
)


USER_ID = 1   # Change this to an actual user ID from your database


print("\n--- ADD ---")
result = add_wardrobe_item(
    user_id=USER_ID,
    name="White T-Shirt",
    category="T-Shirts",
    color="White",
    image_path=None
)
print(result)

item_id = result.get("item_id")


print("\n--- GET ALL ---")
result = get_wardrobe_items(USER_ID)
print(result)


if item_id:
    print("\n--- GET SINGLE ---")
    result = get_wardrobe_item(item_id)
    print(result)


    print("\n--- UPDATE ---")
    result = update_wardrobe_item(
        item_id=item_id,
        name="Updated White T-Shirt",
        category="T-Shirts",
        color="White"
    )
    print(result)


    print("\n--- GET SINGLE AFTER UPDATE ---")
    result = get_wardrobe_item(item_id)
    print(result)


    print("\n--- DELETE ---")
    result = delete_wardrobe_item(item_id)
    print(result)


print("\n--- FINAL DATABASE CHECK ---")
print(get_wardrobe_items(USER_ID))