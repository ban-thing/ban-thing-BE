import json

# list of integer & string
list_1 = [1, 2, 3, "four", "five"]
# convert to Json
json_str = json.dump(list_1)
# displaying
print(type(json_str))