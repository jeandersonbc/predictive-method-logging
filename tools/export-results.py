#!/usr/bin/env python3
import os
import json

from sys import argv

import pandas as pd


if __name__ == "__main__":
    project_name = argv[1]
    results_path = os.path.abspath(os.path.join("out", "ml", project_name))

    scores = []
    for root, _,files in os.walk(results_path):
        for f in files:
            if f == "score.json":
                json_path = os.path.join(root, f)
                with open(json_path) as data:
                    scores.append(json.loads(data.read()))

    merged_score = {}
    for key in scores[0].keys():
        merged_score[key] = []
    for score in scores:
        for k, v in score.items():
            if type(v) is float:
                v = round(v, 2)
                if str(v) == "0.0":
                    v = "<0.00"
            merged_score[k].append(v)

    print(pd.DataFrame(merged_score).to_latex())
