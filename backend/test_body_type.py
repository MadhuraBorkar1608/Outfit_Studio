from cv.body_type import estimate_body_type


chest = input("Enter chest measurement: ")
waist = input("Enter waist measurement: ")
hip = input("Enter hip measurement: ")

result = estimate_body_type(chest, waist, hip)

print("Result:", result)