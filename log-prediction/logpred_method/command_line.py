import argparse

from sklearn.model_selection import train_test_split

import logpred_method


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("model_name", choices="rf ab dt lr et".split(" "))
    parser.add_argument("csv_path")
    parser.add_argument("--balancing", default=None, choices="smote rus".split(" "))
    parser.add_argument("--fraction", default=None, type=float)
    parser.add_argument("--drops", default=[], nargs="+")
    args = parser.parse_args()

    model_name = args.model_name
    csv_path = args.csv_path
    balancing = args.balancing
    fraction = args.fraction
    drops = args.drops

    print(args)

    X, y = logpred_method.load_dataset(fpath=csv_path, drops=drops, fraction=fraction)
    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=0.2,
        random_state=logpred_method.RANDOM_SEED,
        stratify=y,
        shuffle=True,
    )

    logpred_method.run(
        model_name=model_name,
        X_train=X_train,
        X_test=X_test,
        y_train=y_train,
        y_test=y_test,
        balancing=balancing,
    )
