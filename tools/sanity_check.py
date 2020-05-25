import os
import re
from sys import argv


import pandas as pd


def main(argv):
    method_csv = os.path.abspath(argv[1])
    method_df = pd.read_csv(method_csv)

    methods_analysis = int(argv[2])
    print(
        f"methods found from CK={method_df.shape[0]}",
        f"methods found from analysis={methods_analysis}",
        sep="\n"
    )
    assert method_df.shape[0] == methods_analysis

    logged_methods = int(argv[3])
    label_ck = (method_df["logStatementsQty"] > 0).sum()
    print(
        f"logged methods from CK={label_ck}",
        f"logged methods from analysis={logged_methods}",
        sep="\n"
    )
    assert logged_methods == label_ck


if __name__ == "__main__":
    main(argv)
