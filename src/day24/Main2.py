path = "C:\\Users\\ludvi\\IdeaProjects\\Advent of Code 2024\\src\\day24\\"
with open(path + "test.txt", "r") as file:
    test_content = file.read().splitlines()
with open(path + "input.txt", "r") as file:
    input_content = file.read().splitlines()

for i in range(test_content.index(""), len(test_content)):
    if test_content[i] != input_content[i]:
        print(f"Test: {test_content[i]}\nInput: {input_content[i]}\n")