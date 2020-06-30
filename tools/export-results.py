#!/usr/bin/env python3
import json
import os
from sys import argv

import numpy as np
import pandas as pd


def float_formatter(value):
    value = f"{value:.2f}"
    if value == "0.00":
        value = "<0.00"
    return value


def find_files(base_dir, target_file, matcher):
    for root, _, files in os.walk(base_dir):
        for f in files:
            target_path = os.path.join(root, f)
            if f == target_file and matcher(target_path):
                yield target_path


def export_scores(results_path, fn):
    scores = []
    for target_path in find_files(results_path, "score.json", fn):
        with open(target_path) as data:
            scores.append(json.loads(data.read()))

    merged_score = {}
    for key in scores[0].keys():
        merged_score[key] = []
    for score in scores:
        for k, v in score.items():
            merged_score[k].append(v)

    frame = pd.DataFrame(merged_score).sort_values(by=["model", "balancing"])
    ignore_cols = [c for c in list(frame) if "time" in c or "score" in c]
    print("RQ1")
    print("Test set size =", frame["total"][0])
    print(
        frame[frame["balancing"] == "-"]
        .set_index(["model"])
        .drop(columns=["balancing", "total"] + ignore_cols)
        .to_latex(float_format=float_formatter)
    )
    print("RQ2")
    print(
        frame[frame["balancing"] != "-"]
        .set_index(["model", "balancing"])
        .drop(columns=["total"] + ignore_cols)
        .to_latex(float_format=float_formatter)
    )


def export_feature_importance(results_path, fn):
    feature_importance = {}
    for target_path in find_files(results_path, "feature_importance.csv", fn):
        root = os.path.basename(os.path.dirname(target_path))
        feature_importance[os.path.basename(root)] = pd.read_csv(target_path)

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


def export_feature_importance_v2(results_path, fn):
    feature_importance = {}
    for target_path in find_files(results_path, "feature_importance.csv", fn):
        root = os.path.basename(os.path.dirname(target_path))
        if ("rus" not in root) and ("smote" not in root):
            feature_importance[os.path.basename(root)] = pd.read_csv(target_path)

    counting = {}
    n = 3
    for k, v in feature_importance.items():
        print(k)
        print(v.head(n))
        for pos, f in v.head(n)["feature"].items():
            if f not in counting.keys():
                counting[f] = [0 for cnt in range(n)]
            counting[f][pos] += 1

    print(r"\midrule")
    for k, v in sorted(counting.items(), key=lambda item: sum(item[1]), reverse=True):
        print(f"{k} & {sum(v)} & {' & '.join(str(e) for e in v)} \\\\")
    print(r"\bottomrule")


def main():
    project_name = argv[1]
    results_path = os.path.abspath(os.path.join("out", "ml", project_name))

    print("Try-catch removed")
    export_scores(results_path, fn=lambda e: "trycatch" in e)
    export_feature_importance_v2(results_path, fn=lambda e: "trycatch" in e)

    print("With try-catch")
    export_scores(results_path, fn=lambda e: "trycatch" not in e)
    export_feature_importance_v2(results_path, fn=lambda e: "trycatch" not in e)


if __name__ == "__main__":
    main()
