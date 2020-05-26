import os
import re
from sys import argv


import pandas as pd


def main(argv):
    methods_copied = pd.read_csv(os.path.abspath(argv[1]))
    methods_nologs = pd.read_csv(os.path.abspath(argv[2]))
    analysis = pd.read_csv(os.path.abspath(argv[3]))
    print(
        "no-logs",
        f"methods={methods_nologs.shape[0]}",
        f"logged={(methods_nologs['logStatementsQty'] > 0).sum()}",
        f"log_stmts={methods_nologs['logStatementsQty'].sum()}",
    )
    methods_total = methods_copied.shape[0]
    logged_methods = (methods_copied['logStatementsQty'] > 0).sum()
    ratio = (logged_methods / methods_total) * 100
    print(
        "copied",
        f"methods={methods_total}",
        f"logged={logged_methods}",
        f"ratio={ratio:.1f}%",
        f"log_stmts={methods_copied['logStatementsQty'].sum()}",
    )
    print(
        "analysis",
        f"log_stmts={analysis.shape[0]}",
    )


if __name__ == "__main__":
    main(argv)
