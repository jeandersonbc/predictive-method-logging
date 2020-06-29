import os
from sys import argv

import pandas as pd


DATASET_DIR = argv[1]
SUBJECTS = argv[2:]

print(DATASET_DIR)
print(SUBJECTS)


dataframes = []
for subject in SUBJECTS:
    csv_path = os.path.join(DATASET_DIR, subject, "dataset_full.csv")
    if not os.path.exists(csv_path):
        raise Exception(f"Missing CSV path {csv_path}")

    df = pd.read_csv(csv_path)
    print(df.shape)
    
    dataframes.append(df)

final_df = pd.concat(dataframes, ignore_index=True)
final_df.to_csv("dataset_full.csv", index=False)
