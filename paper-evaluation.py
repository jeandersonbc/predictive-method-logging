#!/usr/bin/env python
# coding: utf-8

# In[1]:


import os
import shutil

import pandas as pd
import logpred_method as experiment

from sklearn.model_selection import train_test_split


# Use "FRACTION = None" for full dataset
FRACTION: float = None

# lr: Linear Regression
# ab: Ada Boost
# rf: Random Forest
# dt: Decision Tree
# et: Extra Trees
MODELS = ["lr", "ab", "rf", "dt", "et"]


# You can ignore features on the experiment
IGNORED_FEATURES = ["tryCatchQty_class", "tryCatchQty_method"]


# Stores estimators and feature importances across experiments
ESTIMATORS = {}
FEATURE_IMPORTANCES = {}


# # Utilities

# In[2]:


def merge_scores(scores):
    """
    Returns a merged score from a sequence of scores.
    This is useful to see scores as Pandas DataFrames.
    
    Example:
        in  - [{"a": 1, "b": 2}, {"a": 10, "b": 20}]
        out - {"a": [1, 10], "b": [2, 20]}
    """
    merged = {k:[] for k in scores[0].keys()}
    for score in scores:
        for k, v in score.items():
            merged[k].append(v)

    return merged


# # Experiment CSV and Output directory

# In[3]:


csv_path = os.path.abspath(os.path.join("out", "dataset", "adyen-main", "dataset_full.csv"))

X_adyen, y_adyen = experiment.load_dataset(csv_path, drops=IGNORED_FEATURES, fraction=FRACTION)
X_adyen_train, X_adyen_test, y_adyen_train, y_adyen_test = train_test_split(
    X_adyen, y_adyen, test_size=0.2, stratify=y_adyen, random_state=experiment.RANDOM_SEED
)

output_dir = os.path.abspath(os.path.join("out", "ml", "evaluation"))
if os.path.exists(output_dir):
    shutil.rmtree(output_dir)
os.makedirs(output_dir)


# # RQ 1. What  is  the  performance  of  machine  learning  models in  predicting  log  placement  in  a  large-scale  enterprise system?

# In[4]:


def rq1():
    scores = []
    for model in MODELS:
        out = experiment.run(
            model,
            X_train=X_adyen_train,
            X_test=X_adyen_test,
            y_train=y_adyen_train,
            y_test=y_adyen_test,
            output_to=os.path.join(output_dir, f"rq1-{model}.log")
        )
        estimator, score, fi = out
        scores.append(score)
        
        # Save to the global state this run
        ESTIMATORS[model] = estimator
        FEATURE_IMPORTANCES[model] = fi

    return scores

rq1_scores = rq1()


# ## Results

# In[5]:


results_rq1 = pd.DataFrame.from_dict(merge_scores(rq1_scores)).set_index(["model"])
results_rq1.reset_index().to_csv(
    os.path.join(output_dir, "rq1-results.csv"),
    index=False,
)
results_rq1["prec recall acc tn fp fn tp total".split(" ")]


# # RQ 2. What is the impact of different class balancing strategies on prediction?

# In[6]:


# Similar to rq1 but we include sampling in the experiment now.
def rq2():
    scores = []
    for model in MODELS:
        for balancing in ["smote", "rus"]:
            out = experiment.run(
                model,
                X_train=X_adyen_train,
                X_test=X_adyen_test,
                y_train=y_adyen_train,
                y_test=y_adyen_test,
                balancing=balancing,
                output_to=os.path.join(output_dir, f"rq2-{model}-{balancing}.log")
            )
            estimator, score, fi = out
            scores.append(score)
            
            # Save to the global state this run
            key = f"{model}-{balancing}"
            ESTIMATORS[key] = estimator
            FEATURE_IMPORTANCES[key] = fi

    return scores

rq2_scores = rq2()


# ## Results

# In[7]:


results_rq2 = pd.DataFrame.from_dict(merge_scores(rq2_scores)).set_index(["model", "balancing"])
results_rq2.reset_index().to_csv(
    os.path.join(output_dir, "rq2-results.csv"),
    index=False,
)

relevant_cols = "prec recall acc".split(" ")
results_rq2[relevant_cols]


# Comparative result to the baseline (no balancing). Positive value indicates improvement.

# In[8]:


results_rq2_rel = results_rq2.loc[MODELS, relevant_cols] - results_rq1.loc[MODELS, relevant_cols]
results_rq2_rel.reset_index().to_csv(
    os.path.join(output_dir, "rq2-results-relative.csv"),
    index=False
)
results_rq2_rel


# # RQ 3. How  do  machine  learning  models  perceive  predictors?

# In[9]:


def rank_to_df(rank):
    return pd.DataFrame.from_records(
        [(name, sum(count), *count) for name, count in rank.items()],
        columns="feature total 1st 2nd 3rd".split(" "),
    ).sort_values(by="total 1st 2nd 3rd".split(" "), ascending=False)


def feature_importance_rank(selected_models, top_n=3):
    rank = {}
    for model in selected_models:
        ordered_features = sorted(
            FEATURE_IMPORTANCES[model],
            key=lambda pair: pair[1],
            reverse=True
        )
        for pos, feature_pair, in enumerate(ordered_features[:top_n]):
            feature = feature_pair[0]
            if feature not in rank.keys():
                rank[feature] = [0 for i in range(top_n)]
            rank[feature][pos] += 1
    return rank


# ## Results

# In[10]:


fi = rank_to_df(feature_importance_rank(MODELS))
fi.to_csv(
    os.path.join(output_dir, "rq3-fi-regular.csv"),
    index=False
)
fi


# In[11]:


fi_smote = rank_to_df(
    feature_importance_rank([
        model_key
        for model_key in FEATURE_IMPORTANCES.keys()
        if "smote" in model_key
    ])
)
fi_smote.to_csv(
    os.path.join(output_dir, "rq3-fi-smote.csv"),
    index=False
)
fi_smote


# In[12]:


fi_rus = rank_to_df(
    feature_importance_rank([
        model_key
        for model_key in FEATURE_IMPORTANCES.keys()
        if "rus" in model_key
    ])
)
fi_rus.to_csv(
    os.path.join(output_dir, "rq3-fi-rus.csv"),
    index=False
)
fi_rus


# # RQ 4. How  well  a  model  trained  with  open-source  data  can generalize  to  the  context  of  a  large-scale  enterprise system?

# In[13]:


from typing import List


def selected_apache_projects() -> List[str]:
    """
    Returns the name of the selected Apache projects as listed in the "out/selection" directory.
    """
    selection_dir = os.path.abspath(os.path.join("out", "selection"))
    return sorted([
        selected.replace(".sh", "")
        for selected in os.listdir(selection_dir)
        if selected.endswith(".sh")
    ])


def load_X_y(project: str):
    dataset_path = os.path.abspath(
        os.path.join("out", "dataset", project, "dataset_full.csv")
    )
    X, y = experiment.load_dataset(
        dataset_path, drops=IGNORED_FEATURES
    )
    assert X_adyen.shape[1] == X.shape[1]

    return X, y


APACHE_PROJECTS = {
    project: load_X_y(project)
    for project in selected_apache_projects()
}

assert len(APACHE_PROJECTS) == 29


# In[14]:


for k, v in APACHE_PROJECTS.items():
    print(f"{k:20} {str(v[0].shape):>15}")


# ## Learning from all Apache projects

# In[15]:


X_apache_all = pd.concat(
    [X_apache for X_apache, _ in APACHE_PROJECTS.values()],
    ignore_index=True,
)
y_apache_all = pd.concat(
    [y_apache for _, y_apache in APACHE_PROJECTS.values()],
    ignore_index=True,
)

# Sum of entries must be equals to the number of final entries
assert sum([X.shape[0] for X, _ in APACHE_PROJECTS.values()]) == X_apache_all.shape[0]

# apache dataset size, all together
X_apache_all.shape


# In[ ]:


def rq4():
    scores = []
    model = "rf"
    out = experiment.run(
        model,
        X_train=X_apache_all,
        X_test=X_adyen_test,
        y_train=y_apache_all,
        y_test=y_adyen_test,
        output_to=os.path.join(output_dir, f"rq4-{model}-apache-all.log"),
        tuning_enabled=True
    )
    estimator, score, fi = out
    score["project"] = "apache-all"
    score["training_size"] = X_apache_all.shape[0]
    scores.append(score)

    # Save to the global state this run
    key = f"{model}-apache-all"
    ESTIMATORS[key] = estimator
    FEATURE_IMPORTANCES[key] = fi

    return scores


rq4_scores_all = rq4()


# ## Learning from Projects Individually

# In[ ]:


def rq4_individual():
    scores = []
    model = "rf"
    for project, Xy in APACHE_PROJECTS.items():
        out = experiment.run(
            model,
            X_train=Xy[0].drop(columns=["type"]),
            X_test=X_adyen_test.drop(columns=["type"]),
            y_train=Xy[1].drop(columns=["type"]),
            y_test=y_adyen_test.drop(columns=["type"]),
            output_to=os.path.join(output_dir, f"rq4-{model}-{project}.log"),
            tuning_enabled=True
        )
        estimator, score, fi = out
        score["project"] = project
        score["training_size"] = Xy[0].shape[0]
        scores.append(score)

        # Save to the global state this run
        key = f"{model}-{project}"
        ESTIMATORS[key] = estimator
        FEATURE_IMPORTANCES[key] = fi

    return scores


rq4_scores_individual = rq4_individual()


# ## Results

# In[ ]:


results_rq4 = pd.DataFrame.from_dict(
    merge_scores(
        rq4_scores_all + rq4_scores_individual
    )
)
results_rq4.to_csv(
    os.path.join(output_dir, "rq4.csv"),
    index=False
)
results_rq4.drop(columns=["model", "balancing"]).sort_values(by="prec recall acc".split(" "), ascending=False)

