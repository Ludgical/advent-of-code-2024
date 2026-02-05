path = "C:\\Users\\ludvi\\IdeaProjects\\Advent of Code 2024\\src\\day24\\test.txt"
with open(path, "r") as file:
    content = file.read()
with open(path, "w") as file:
    new_content = ""
    for x in range(45):
        new_content += f"x{x:02}: {1 if x % 2 == 1 else 0}\n"
    for y in range(45):
        new_content += f"y{y:02}: {0 if y % 2 == 1 else 1}\n"
    new_content += "\n" + content.split("\n\n")[1]
    file.write(new_content.replace("\r", ""))