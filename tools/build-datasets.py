import os
from sys import argv

import pandas as pd


def merge(dfx: pd.DataFrame, dfy: pd.DataFrame) -> pd.DataFrame:
    class_df = dfx.drop(columns=["modifiers", "tcc", "lcc", "logStatementsQty"])
    methods_df = dfy.drop(columns=["modifiers", "hasJavaDoc"])
    print({e for e in class_df["file"].head().to_list()})
#    print(methods_df["file"].head())
#    return pd.merge(class_df, methods_df, on=["file", "class"


def prepare_labels(dfx: pd.DataFrame, labels: pd.DataFrame) -> None:
    pass


def main(argv):
    nolog_path = argv[1]
    copied_path = argv[2]
    
    nolog_class = pd.read_csv(os.path.join(nolog_path, "class.csv"))
    nolog_method = pd.read_csv(os.path.join(nolog_path, "method.csv"))
    labels_method = pd.read_csv(os.path.join(copied_path, "method.csv"))
    print(nolog_path)
    assert labels_method.shape == nolog_method.shape

    merged = merge(nolog_class, nolog_method)
    assert merged.shape[0] == nolog_method.shape[0]


if __name__ == "__main__":
    main(argv)

