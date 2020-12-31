import os, re
from sys import argv

import pandas as pd


def merge(dfx: pd.DataFrame, dfy: pd.DataFrame) -> pd.DataFrame:
    class_df = dfx.drop(columns=["modifiers", "tcc", "lcc", "logStatementsQty"])
    methods_df = dfy.drop(columns=["modifiers", "hasJavaDoc", "line"])
    return pd.merge(
        methods_df, class_df, on=["file", "class"], suffixes=("_method", "_class")
    ).drop_duplicates()


def prepare_labels(dfx: pd.DataFrame, labels: pd.DataFrame) -> pd.DataFrame:
    return pd.merge(
        dfx, labels, on=["file", "method", "class"], suffixes=("_orig", "_label")
    ).drop_duplicates()


def main(argv):
    nolog_path = argv[1]
    copied_path = argv[2]

    # Loads DataFrames and adjust annoying file column
    nolog_preffix = "/".join(nolog_path.split("/")[-2:])
    nolog_preffix = f".*out/log-removal/{nolog_preffix}"
    copied_preffix = "/".join(copied_path.split("/")[-2:])
    copied_preffix = f".*out/log-removal/{copied_preffix}"

    nolog_class = pd.read_csv(os.path.join(nolog_path, "class.csv"))
    nolog_class["file"] = nolog_class["file"].apply(
        lambda e: re.sub(nolog_preffix, ".", e)
    )
    nolog_method = pd.read_csv(os.path.join(nolog_path, "method.csv"))
    nolog_method["file"] = nolog_method["file"].apply(
        lambda e: re.sub(nolog_preffix, ".", e)
    )
    labels_method = pd.read_csv(os.path.join(copied_path, "method.csv"))[
        ["file", "class", "method", "logStatementsQty"]
    ]
    labels_method["file"] = labels_method["file"].apply(
        lambda e: re.sub(copied_preffix, ".", e)
    )
    # Merging, and so on...
    merged = merge(nolog_class, nolog_method)
    assert merged.shape[0] == nolog_method.shape[0]

    merged = prepare_labels(merged, labels_method)
    assert merged.shape[0] <= nolog_method.shape[0], "Should't have more data points than expected!"

    merged["label"] = merged["logStatementsQty_label"] > 0
    dataset_full = merged.drop(
        columns=[c for c in list(merged) if "logStatementsQty_label" in c]
    )
    dataset_full.to_csv("dataset_full.csv", index=False)

    print("Original labels (BEFORE removing logs)")
    print(
        f"methods={labels_method.shape[0]:,d}",
        f"logged_methods={(labels_method['logStatementsQty'] > 0).sum():,d}",
        f"ratio={(labels_method['logStatementsQty'] > 0).mean():.2f}",
        f"log_statments={labels_method['logStatementsQty'].sum():,d}",
    )
    print("Dataset (AFTER removing logs)")
    print(
        f"methods={dataset_full.shape[0]:,d}",
        f"logged_methods={dataset_full['label'].sum():,d}",
        f"ratio={dataset_full['label'].mean():.2f}",
        f"noise={(dataset_full['logStatementsQty_orig'] > 0).sum():,d}",
        f"missed_log_stmts={dataset_full['logStatementsQty_orig'].sum():,d}",
    )


if __name__ == "__main__":
    main(argv)
