#!/usr/bin/env python3
import os
import json

from sys import argv

import pandas as pd
import numpy as np


def float_formatter(value):
    value = f"{value:.2f}"
    if value == "0.00":
        value = "<0.00"
    return value


def export_scores(results_path):
    scores = []
    for root, _, files in os.walk(results_path):
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
            merged_score[k].append(v)

    print(
        pd.DataFrame(merged_score).to_latex(index=False, float_format=float_formatter)
    )


def export_feature_importance(results_path):
    feature_importance = {}
    for root, _, files in os.walk(results_path):
        for f in files:
            if f == "feature_importance.csv":
                csv = os.path.join(root, f)
                feature_importance[os.path.basename(root)] = pd.read_csv(csv)

    for k, v in feature_importance.items():
        print(k)
        print(v.head())

    header = pd.MultiIndex.from_product(
        [feature_importance.keys(), ["feature", "importance"]]
    )
    values = np.concatenate(
        [v.head().values for v in feature_importance.values()], axis=1
    )
    print(
        pd.DataFrame(values, columns=header).to_latex(
            index=False, float_format=float_formatter
        )
    )


if __name__ == "__main__":
    project_name = argv[1]
    results_path = os.path.abspath(os.path.join("out", "ml", project_name))
    export_scores(results_path)
    export_feature_importance(results_path)
