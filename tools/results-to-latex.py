from sys import argv
from os.path import join

import pandas as pd


def int_formatter(num):
    return f"{num:,d}"


# Path to the directory containing CSV files for rq1, rq2, rq3, and rq4
data_dir = argv[1]

cols = "acc,prec,recall,tn,fp,fn,tp,total".split(",")

rq1 = (
    pd.read_csv(join(data_dir, "rq1-results.csv"))
    .set_index("model")[cols]
    .sort_values(by=["acc", "prec", "recall"], ascending=False)
)
print("RQ1")
print(
    rq1.to_latex(
        float_format="%.2f",
        formatters={col: int_formatter for col in "tn,fp,fn,tp".split(",")},
    )
)
print(
    (rq1["prec recall acc".split(" ")].agg(["mean", "std"]) * 100).to_latex(
        float_format="%.1f"
    )
)

print("RQ2")
rq2 = pd.read_csv(join(data_dir, "rq2-results-relative.csv")).set_index(
    ["model", "balancing"]
)["acc prec recall".split(" ")]
print(rq2.to_latex(float_format="%+.2f"))
print(
    (
        rq2.reset_index()
        .drop(columns=["model"])
        .groupby("balancing")
        .agg(["mean", "std"])
        * 100
    ).to_latex(float_format="%.1f")
)

print("RQ3")
rq3 = pd.read_csv(join(data_dir, "rq3-results.csv"))
print(rq3[rq3["total"] > 1].to_latex(index=False))

print("RQ4")
rq4 = (
    pd.read_csv(join(data_dir, "rq4-results.csv"))
    .sort_values(by=["acc", "prec", "recall"], ascending=False)
    .set_index(["project"])[["training_size"] + cols]
)
print(
    rq4.drop(columns="total tp tn fp fn".split(" ")).to_latex(
        float_format="%.2f", formatters={"training_size": int_formatter}
    )
)
