#!/usr/bin/env python3
import json
import re
from sys import argv

import pandas as pd

# Do verification based on file, class, and method
cols = ["file", "class", "method"]

TEXTUAL_FEATURES_PATH = argv[1]
DATASET_PATH = argv[2]

# data from CK
df = pd.read_csv(DATASET_PATH)[cols]

# We need a little tweak on the class Series from the dataset
# CK collects fully qualified class names, while the token extractor don't
df["class"] = df["class"].apply(
    lambda e: re.sub(r".*\.", "", e)
)
df = df.sort_values(by=cols).reset_index().drop(columns=["index"])
print(df.head())

# data from JSON
with open(TEXTUAL_FEATURES_PATH) as f:
    project = json.load(f)

# transform JSON to the expected DataFrame
flatten_data = {k: [] for k in cols}
for src in project:
    for data in src["data"]:
        clazz, method = data["methodName"].split("::")

        flatten_data["file"].append(src["fileName"])
        flatten_data["class"].append(clazz)
        flatten_data["method"].append(method)

df_json = pd.DataFrame(flatten_data).sort_values(by=cols).reset_index().drop(columns=["index"])
print(df_json.head())

# Check
print(df.shape, df_json.shape)
are_equals = df.equals(df_json)
print(are_equals)
if not are_equals:
    print("dumping dataframes for row-by-row inspection. use diff")
    df_json.to_csv("dump-json-df.csv", index=False)
    df.to_csv("dump-df.csv", index=False)
    exit(1)
