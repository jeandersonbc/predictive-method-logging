#!/usr/bin/env python3
import os
from sys import argv

import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np

from matplotlib.offsetbox import AnchoredText


def merge_common_enclosing_contexts(df: pd.DataFrame) -> None:
    grouping_cat = {
        "loop": {
            "enhanced_for_statement",
            "while_statement",
            "for_statement",
            "do_statement",
        },
        "method": {"method_declaration", "static_initializer"},
        "others": {
            "labeled_statement",
            "synchronized_statement",
            "lambda_expr",
            "switch_statement",
        },
    }
    for replacement, tomerge in grouping_cat.items():
        selection = df["enclosing_context"].apply(lambda e: e in tomerge)
        df.loc[selection, ["enclosing_context"]] = replacement


def compute_dist(df: pd.DataFrame) -> pd.DataFrame:
    merge_common_enclosing_contexts(df)
    dist = (
        df.drop(columns=["total_log_stmts", "method", "class"])
        .rename(columns={"enclosing_context": "type", "file": "count"})
        .groupby("type")
        .agg(len)
        .reset_index()
    )
    total = dist["count"].sum()
    dist["perc"] = dist["count"] / total * 100

    return dist.sort_values(by="count", ascending=False)


def analyze_trycatch_ifelse(dist: pd.DataFrame):
    ifelse_stmt = dist["type"] == "ifelse_statement"
    catch_cls = dist["type"] == "catch_clause"
    try_stmt = dist["type"] == "try_statement"

    belongs_condition = ifelse_stmt | catch_cls | try_stmt

    total_belongs = dist.loc[belongs_condition, ["count", "perc"]].sum()
    total_otherwise = dist.loc[~belongs_condition, ["count", "perc"]].sum()

    # pretty printing distribution
    print("{:>21} {:>2}".format(*"N %".split(" ")))
    for name, data in zip(
        ["trycatch-ifelse", "otherwise"], [total_belongs, total_otherwise]
    ):
        print(f"{name:15} {int(data['count']):5} {data['perc']:.0f}")


def compute_logstmts_per_method_dist(df: pd.DataFrame) -> pd.DataFrame:
    return (
        df.drop(columns=["total_log_stmts"])
        .groupby(["file", "class", "method"])
        .count()
        .rename(columns={"enclosing_context": "methods"})
        .reset_index()
    )


def plot_distribution(dist, output):
    data = np.log2(dist["methods"])
    description = data.describe()
    print(description)

    fig, ax = plt.subplots(dpi=300)
    sns.violinplot(data=data, color="aliceblue", ax=ax, cut=0, orient="h")
    ax.set_aspect(2)

    fontsize = 12
    locs = np.arange(description["min"], description["max"] + 1)
    plt.xticks(locs, ["$2^{%d}$" % int(d) for d in locs], fontsize=fontsize)
    ax.set_xlabel(r"Number of Log Statements [$log_2$]", fontsize=fontsize)
    ax.set_xlim((description["min"] - 0.1, description["max"] + 0.1))
    ax.set_ylabel("")
    ax.set(yticklabels=[])

    texts = [
        "  ".join(
            [
                f"N={int(description['count']):,d}",
                f"min={float(description['min']):.2f}",
                f"max={float(description['max']):.2f}",
            ]
        ),
        "    ".join(
            [
                f"Q$_1$={float(description['25%']):.2f}",
                f"Q$_2$={float(description['50%']):.2f}",
                f"Q$_3$={float(description['75%']):.2f}",
            ]
        ),
    ]
    ax.add_artist(
        AnchoredText("\n".join(texts), loc="upper right", prop={"fontsize": fontsize})
    )

    plt.tight_layout()
    plt.savefig(output)


def main(csv_file):
    output_path = os.path.dirname(csv_file)
    df = pd.read_csv(csv_file)

    dist = compute_dist(df)
    print(dist)
    total = dist["count"].sum()
    print(f"Total number of log statements = {total}")

    print("Try-catch and if-else contribution:")
    analyze_trycatch_ifelse(dist)

    print("Latex table (paper purposes)")
    print(dist.to_latex(formatters={
        "perc": lambda e: f"{round(e):,d}",
        "count": lambda e: f"{e:,d}",
    }))

    print("Distribution of log statements per method")
    dist_stmts = compute_logstmts_per_method_dist(df)
    print(dist_stmts.describe())
    plot_distribution(dist_stmts, os.path.join(output_path, "dist-logstmts.pdf"))


if __name__ == "__main__":
    # sns.set(style="whitegrid")
    sns.set()
    main(argv[1])
