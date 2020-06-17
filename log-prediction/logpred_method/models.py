import numpy as np
from sklearn.compose import make_column_transformer
from sklearn.ensemble import (
    AdaBoostClassifier,
    ExtraTreesClassifier,
    RandomForestClassifier,
)
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import (
    OneHotEncoder,
    RobustScaler,
    StandardScaler,
    MinMaxScaler,
    Normalizer,
)
from sklearn.tree import DecisionTreeClassifier

RANDOM_SEED = 2357


def load_random_forest(categ_cols):
    params = {
        "clf__n_estimators": [10, 50, 100, 150, 200],
        "clf__criterion": ["gini", "entropy"],
        "clf__max_depth": [3, 6, 12, 24, None],
        "clf__max_features": ["auto", "sqrt", "log2", None],
        "clf__min_samples_split": [2, 3, 4, 5, 10],
        "clf__bootstrap": [True, False],
    }
    # Model init
    model = RandomForestClassifier(random_state=RANDOM_SEED)

    # Data processing and Pipeline
    pipeline = Pipeline(
        steps=[
            (
                "transformer",
                make_column_transformer(
                    (OneHotEncoder(), categ_cols), remainder="passthrough"
                ),
            ),
            ("clf", model),
        ]
    )
    return (
        pipeline,
        params,
    )


def load_extra_trees(categ_cols):
    params = {
        "clf__n_estimators": [10, 50, 100, 150, 200],
        "clf__criterion": ["gini", "entropy"],
        "clf__max_depth": [3, 6, 12, 24, None],
        "clf__max_features": ["auto", "sqrt", "log2", None],
        "clf__min_samples_split": [2, 3, 4, 5, 10],
        "clf__bootstrap": [True, False],
    }

    # Model init
    model = ExtraTreesClassifier(random_state=RANDOM_SEED)

    # Data processing and Pipeline
    pipeline = Pipeline(
        steps=[
            (
                "transformer",
                make_column_transformer(
                    (OneHotEncoder(), categ_cols), remainder="passthrough"
                ),
            ),
            ("clf", model),
        ]
    )
    return (
        pipeline,
        params,
    )


def load_decision_tree(categ_cols):
    params = {
        "clf__criterion": ["gini", "entropy"],
        "clf__splitter": ["best", "random"],
        "clf__max_depth": [3, 6, 12, 24, None],
        "clf__max_features": ["auto", "sqrt", "log2", None],
        "clf__min_samples_split": [2, 3, 5, 10, 11],
    }

    # Model init
    model = DecisionTreeClassifier(random_state=RANDOM_SEED)

    # Data processing and Pipeline
    pipeline = Pipeline(
        steps=[
            (
                "transformer",
                make_column_transformer(
                    (OneHotEncoder(), categ_cols), remainder="passthrough"
                ),
            ),
            ("clf", model),
        ]
    )
    return (
        pipeline,
        params,
    )


def load_ada_boost(categ_cols):
    params = {
        "clf__n_estimators": [10, 50, 100, 150, 200],
        "clf__learning_rate": [0.0001, 0.001, 0.01, 0.1, 1.0],
        "clf__algorithm": ["SAMME", "SAMME.R"],
    }

    # Model init
    model = AdaBoostClassifier(random_state=RANDOM_SEED)

    # Data processing and Pipeline
    pipeline = Pipeline(
        steps=[
            (
                "transformer",
                make_column_transformer(
                    (OneHotEncoder(), categ_cols), remainder="passthrough"
                ),
            ),
            ("clf", model),
        ]
    )
    return (
        pipeline,
        params,
    )


def load_logreg(categ_cols):
    r = np.random.RandomState(seed=RANDOM_SEED)

    params = {
        "clf__max_iter": np.arange(250, 350, 10),
        "clf__C": r.uniform(0.01, 10, 10),
        "transformer__remainder": [
            RobustScaler(),
            StandardScaler(),
            MinMaxScaler(),
            Normalizer(),
        ],
    }

    # Model init
    model = LogisticRegression(random_state=RANDOM_SEED)

    # Data processing and Pipeline
    pipeline = Pipeline(
        steps=[
            (
                "transformer",
                make_column_transformer(
                    (OneHotEncoder(), categ_cols), remainder=RobustScaler()
                ),
            ),
            ("clf", model),
        ]
    )
    return (
        pipeline,
        params,
    )


def init_pipeline(categ_cols, model_param):
    if model_param == "rf":
        return load_random_forest(categ_cols)
    if model_param == "dt":
        return load_decision_tree(categ_cols)
    if model_param == "et":
        return load_extra_trees(categ_cols)
    if model_param == "ab":
        return load_ada_boost(categ_cols)
    if model_param == "lr":
        return load_logreg(categ_cols)
    else:
        raise Exception(f"Unkown parameter {model_param}")
