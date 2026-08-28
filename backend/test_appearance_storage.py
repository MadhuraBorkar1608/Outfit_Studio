from services.appearance_service import save_appearance_analysis


user_id = input("Enter user ID: ")

result = save_appearance_analysis(
    user_id=int(user_id),
    face_shape="Oval",
    skin_tone="Light",
    body_type="Hourglass"
)

print("Storage result:", result)