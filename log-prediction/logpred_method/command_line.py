import argparse

import logpred_method


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("model_name", choices="rf ab dt lr et".split(" "))
    parser.add_argument("csv_path")
    parser.add_argument("--balancing", default=None, choices="smote rus".split(" "))
    parser.add_argument("--fraction", default=None, type=float)
    args = parser.parse_args()

    model_name = args.model_name
    csv_path = args.csv_path
    balancing = args.balancing
    fraction = args.fraction

    logpred_method.run(model_name=model_name, csv_path=csv_path, balancing=balancing, fraction=fraction)
