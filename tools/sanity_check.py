import os
import re
from sys import argv


import pandas as pd


def main(argv):
    methods_copied = pd.read_csv(os.path.abspath(argv[1]))
    methods_nologs = pd.read_csv(os.path.abspath(argv[2]))

    methods_total = methods_nologs.shape[0]
    logged_methods = (methods_nologs['logStatementsQty'] > 0).sum()
    ratio = (logged_methods / methods_total) * 100
    print(
        "no-logs",
        f"methods={methods_total}",
        f"logged={logged_methods}",
        f"ratio={ratio:.1f}",
        f"log_stmts={methods_nologs['logStatementsQty'].sum()}",
    )
    methods_total = methods_copied.shape[0]
    logged_methods = (methods_copied['logStatementsQty'] > 0).sum()
    ratio = (logged_methods / methods_total) * 100
    print(
        "copied",
        f"methods={methods_total}",
        f"logged={logged_methods}",
        f"ratio={ratio:.1f}",
        f"log_stmts={methods_copied['logStatementsQty'].sum()}",
    )

    analysis_output_file = os.path.abspath(argv[3])
    with open(analysis_output_file) as f:
        lines = [line.strip() for line in f][-3:-1]

    logStatements= lines[0].split(" ")[0]
    fields= lines[1].split(" ")
    print(
        "analysis",
        f"methods={fields[1]}",
        f"logged={fields[3]}",
        f"ratio={int(fields[3]) / int(fields[1]) * 100:.1f}",
        f"log_stmts={logStatements}",
    )

if __name__ == "__main__":
    main(argv)
