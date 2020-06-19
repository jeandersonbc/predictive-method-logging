import numpy as np
from imblearn.over_sampling import SMOTE
from imblearn.pipeline import Pipeline as ImbPipeline
from imblearn.under_sampling import RandomUnderSampler
from sklearn.compose import make_column_transformer
from sklearn.ensemble import (
    AdaBoostClassifier,
    ExtraTreesClassifier,
    RandomForestClassifier,
)
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import (
    MinMaxScaler,
    Normalizer,
    OneHotEncoder,
    RobustScaler,
    StandardScaler,
)
from sklearn.tree import DecisionTreeClassifier

RANDOM_SEED = 2357


def load_random_forest():
    params = {
        "clf__n_estimators": [10, 50, 100, 150, 200],
        "clf__criterion": ["gini", "entropy"],
        "clf__max_depth": [3, 6, 12, 24, None],
        "clf__max_features": ["auto", "sqrt", "log2", None],
        "clf__min_samples_split": [2, 3, 4, 5, 10],
        "clf__bootstrap": [True, False],
    }
    return RandomForestClassifier(random_state=RANDOM_SEED), params


def load_extra_trees():
    params = {
        "clf__n_estimators": [10, 50, 100, 150, 200],
        "clf__criterion": ["gini", "entropy"],
        "clf__max_depth": [3, 6, 12, 24, None],
        "clf__max_features": ["auto", "sqrt", "log2", None],
        "clf__min_samples_split": [2, 3, 4, 5, 10],
        "clf__bootstrap": [True, False],
    }
    return ExtraTreesClassifier(random_state=RANDOM_SEED), params


def load_decision_tree():
    params = {
        "clf__criterion": ["gini", "entropy"],
        "clf__splitter": ["best", "random"],
        "clf__max_depth": [3, 6, 12, 24, None],
        "clf__max_features": ["auto", "sqrt", "log2", None],
        "clf__min_samples_split": [2, 3, 5, 10, 11],
    }
    return DecisionTreeClassifier(random_state=RANDOM_SEED), params


def load_ada_boost():
    params = {
        "clf__n_estimators": [10, 50, 100, 150, 200],
        "clf__learning_rate": [0.0001, 0.001, 0.01, 0.1, 1.0],
        "clf__algorithm": ["SAMME", "SAMME.R"],
    }
    return AdaBoostClassifier(random_state=RANDOM_SEED), params


def load_logreg():
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
    return LogisticRegression(random_state=RANDOM_SEED), params


def create_pipeline(categ_cols, model_param, balancing=None):
    if model_param == "rf":
        model, params = load_random_forest()
    elif model_param == "dt":
        model, params = load_decision_tree()
    elif model_param == "et":
        model, params = load_extra_trees()
    elif model_param == "ab":
        model, params = load_ada_boost()
    elif model_param == "lr":
        model, params = load_logreg()
    else:
        raise Exception(f"Unkown parameter {model_param}")

    # Common pipeline steps
    column_transformer = make_column_transformer(
        (OneHotEncoder(), categ_cols), remainder="passthrough"
    )
    pipeline_steps = [("transformer", column_transformer), ("clf", model)]

    # Actual pipeline instantiation
    if balancing is None:
        pipeline = Pipeline(steps=pipeline_steps)
    elif balancing in {"smote", "rus"}:
        sampler = RandomUnderSampler(random_state=RANDOM_SEED)
        if balancing == "rus":
            sampler = SMOTE(random_state=RANDOM_SEED)
        pipeline_steps.insert(1, ("sampler", sampler))
        pipeline = ImbPipeline(steps=pipeline_steps)
    else:
        raise Exception(f"Unknown balancing {balancing}")

    return pipeline, params
