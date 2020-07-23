from os.path import abspath, join

import pandas as pd


def int_formatter(num):
    return f"{num:,d}"


data_dir = abspath(join("out", "ml", "evaluation-tuning-True"))

cols = "prec,recall,acc,tn,fp,fn,tp,total".split(",")

rq1 = pd.read_csv(join(data_dir, "rq1-results.csv")).set_index("model")[cols]
print(rq1.to_latex(float_format="%.2f"))
print((rq1["prec recall acc".split(" ")].mean() * 100).to_latex(float_format="%.1f"))
print((rq1["prec recall acc".split(" ")].std() * 100).to_latex(float_format="%.1f"))

rq2 = pd.read_csv(join(data_dir, "rq2-results-relative.csv")).set_index(
    ["model", "balancing"]
)["prec recall acc".split(" ")]
print(rq2.to_latex(float_format="%+.2f"))

rq4 = (
    pd.read_csv(join(data_dir, "rq4.csv"))
    .sort_values(by="training_size", ascending=False)
    .set_index(["project"])[["training_size"] + cols]
)
print(
    rq4.drop(columns="fp fn tn tp total".split(" ")).to_latex(
        float_format="%.2f", formatters={"training_size": int_formatter}
    )
)
