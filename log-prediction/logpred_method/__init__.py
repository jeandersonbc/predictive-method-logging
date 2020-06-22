import json
import warnings

import pandas as pd
from sklearn.metrics import (
    confusion_matrix,
    precision_score,
    recall_score,
    roc_auc_score,
)
from sklearn.model_selection import RandomizedSearchCV, train_test_split

from logpred_method.models import create_pipeline

RANDOM_SEED = 2357

# Suppresses non-convergence warnings from LogisticRegression
warnings.warn = lambda *args, **kwargs: None


def load_dataset(fpath, remove_noise=False, fraction=None):
    df = pd.read_csv(fpath)
    if remove_noise:
        df = df[~(df["logStatementsQty_orig"] > 0)]
    df.set_index(["file", "class", "method"], inplace=True)
    df = df.drop(columns=["logStatementsQty_orig"])
    # Dataset reduction to for dev purposes
    if fraction:
        df = df.sample(frac=fraction, random_state=RANDOM_SEED)
    return df


def print_stats(data):
    num_methods = data.shape[0]
    num_labels = (data["label"] > 0).sum()
    balancing = (num_labels / num_methods) * 100
    print(
        f"methods={num_methods}",
        f"logged_methods={num_labels}",
        f"ratio={balancing:.1f}",
    )


def make_score(y_test, pred):
    tn, fp, fn, tp = confusion_matrix(y_test, pred).ravel()
    return {
        "prec": precision_score(y_test, pred),
        "recall": recall_score(y_test, pred),
        "roc_auc": roc_auc_score(y_test, pred),
        "tn": tn,
        "fp": fp,
        "fn": fn,
        "tp": tp,
        "total": sum([tn, fp, fn, tp]),
    }


def extract_feature_importance(pipe, categ, numerical):
    clf = pipe.named_steps["clf"]
    transformer = pipe.named_steps["transformer"]

    importances = None
    # Regression-like algorithms
    if hasattr(clf, "coef_"):
        importances = clf.coef_[0]
    # Tree-based algorithms
    if hasattr(clf, "feature_importances_"):
        importances = clf.feature_importances_

    feature_names = []
    categ_encoder = transformer.named_transformers_["onehotencoder"]
    feature_names.extend(categ_encoder.get_feature_names(categ))
    feature_names.extend(numerical)

    return [
        (f, i)
        for f, i in sorted(
            zip(feature_names, importances), key=lambda fi: fi[1], reverse=True
        )
    ]


def save_score(score):
    with open("score.json", "w") as f:
        json.dump(score, f)


def save_feature_importance(data):
    as_dict = {"feature": [], "importance": []}
    for (ftr, imp) in data:
        as_dict["feature"].append(ftr)
        as_dict["importance"].append(imp)
    pd.DataFrame.from_dict(as_dict).to_csv("feature_importance.csv", index=False)


def run(model_name, csv_path, balancing=None, fraction=None, drops=()):
    data = load_dataset(csv_path, fraction=fraction)

    # Train(80%) Test (20%) split
    X = data.drop(columns=["label"])
    y = data["label"]

    dropped_features = [col for col in drops if col in set(data.columns)]
    X.drop(columns=dropped_features, inplace=True)
    assert len(list(X)) > 0, "Unable to use empty data frame"

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=RANDOM_SEED, stratify=y, shuffle=True
    )
    train_test_info = {
        "train_n": X_train.shape[0],
        "train_ratio": y_train.mean() * 100,
        "test_n": X_test.shape[0],
        "test_ratio": y_test.mean() * 100,
    }
    print(pd.Series(train_test_info))

    categ_cols = list(X.dtypes[~(X.dtypes == "int64")].index)
    num_cols = list(X.dtypes[X.dtypes == "int64"].index)
    print("qualitative:", len(categ_cols), categ_cols)
    print("quantitative:", len(num_cols), num_cols)

    pipe, params = create_pipeline(categ_cols, model_name, balancing)

    # Hyper-parameter tunning
    search = RandomizedSearchCV(
        estimator=pipe,
        param_distributions=params,
        n_iter=10,
        scoring="roc_auc",
        cv=5,
        random_state=RANDOM_SEED,
    )
    print(search)

    # Training
    search.fit(X_train, y_train)

    print("Best params", pd.Series(search.best_params_), sep="\n")
    cv_results_best = pd.DataFrame.from_dict(search.cv_results_).loc[search.best_index_]
    print(cv_results_best)

    # Prediction
    pred = search.predict(X_test)

    # Score
    score = make_score(y_test, pred)
    score["mean_fit_time"] = cv_results_best["mean_fit_time"]
    score["std_fit_time"] = cv_results_best["std_fit_time"]
    score["mean_test_score"] = cv_results_best["mean_test_score"]
    score["std_test_score"] = cv_results_best["std_test_score"]

    # Unpacks numpy data types
    score = {k: v.item() for k, v in score.items()}

    score["model"] = model_name
    score["balancing"] = balancing if balancing is not None else "-"
    save_score(score)
    print("SCORE:", score)

    # Feature Importance
    pipe = search.best_estimator_
    fi = extract_feature_importance(pipe, categ_cols, num_cols)
    for f, i in fi[:5]:
        print(f, i)
    save_feature_importance(fi)
