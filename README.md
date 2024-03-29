Predictive Method Logging
-----

[![Build Status](https://travis-ci.com/jeandersonbc/predictive-method-logging.svg?token=VmF136TKUqZzBdun2K3C&branch=master)](https://travis-ci.com/jeandersonbc/predictive-method-logging)

Learning whether a method should be logged or not based on code metrics.

## Minimum requirements
* Bash environment to run scripts
* Java >= 8.0
* Python >= 3.6 with pip available

The subject selection step in our experiments relies on a `R` script with the `dplyr` package available. This is only required if you are interested in replicating our study or trying different selection criteria.

## Important

* We highly recommend you to create a Python virtual environment in your working directory before starting:
  * `python3 -m venv .venv && source .venv/bin/activate`

* _"Where is the dataset?"_
  * Raw data is not provided for practical reasons; however, the process to generate and analyze data is fully automated.
  * Source files and data related to our industry partner are confidential and unavailable.


## Getting Started

Getting started is easy as 1, 2, 3:

| Step | What | How |
|:----:|:------------|:----|
|1|Install the Python dependencies | `pip3 install -r requirements.txt` |
|2| Build the project components | `./gradlew deploy-aux-tools` |
|3| Get the selected list of Apache projects (~3.5 Gb) | `./gradlew fetch-projects-paper` |


Some Python scripts depends on the dependencies installed from Step 1 (e.g., Pandas and Numpy) to analyze intermediate data during experimentation.
Step 3 will download 29 Apache projects from [a CSV file](./apache-projects-paper.csv) into a `apache-downloads` dir. Also, it will generate a `apache-projects` dir with scripts that exports the absolute path and revision of a given project.
If you want to download [the initial list of all 69 Apache
projects](./apache-projects-all.csv), run `./gradlew fetch-apache-projects` instead.
Keep in mind that the full list contains ~7GB.

#### Hello World

By now, you should be able have some fun. Try to process the project Apache Commons BeansUtils:

```{bash}
> ./run-single.sh apache-projects/commons-beanutils.sh
```

What happenend?
1. Scripts classified java source files
2. Scripts analyzed the presence of log statements in those files
    * Num. of log statements, where they were placed, and how they are distributed
3. Created a copy of the analyzed files and removed log statements for feature extraction

This is the expected output:
```{bash}
> find out -d 2 -type d
out/analysis/commons-beanutils      # Analysis of source files
out/dataset/commons-beanutils       # Contains the final dataset for machine learning experiments
out/log-removal/commons-beanutils   # Contains a copy of source files without log statements
out/codemetrics/commons-beanutils   # Contains code metrics of the analyzed files
```

With the generated CSV, you can reuse our machine learning package in a interactive environment:

```
>>> import logpred_method
>>> X, y = logpred_method.load_dataset("out/dataset/commons-beanutils/dataset_full.csv")
>>> from sklearn.model_selection import train_test_split
>>> X_train, X_test, y_train, y_test = train_test_split(X, y, stratify=y, test_size=0.2, random_state=0)
>>> out = logpred_method.run("rf", X_train, X_test, y_train, y_test, output_to="output.log", tuning_enabled=False)
```

For details about replicating our study, see [docs/Paper Evaluation - Tuning Enabled.pdf](./docs/Paper%20Evaluation%20-%20Tuning%20Enabled.pdf).

Feel free to use the generated dataset with your preferred ML library.
We encourage you to explore your own ML training process and compare it with our results.

## Components

1. `log-identifier`: Cross-project utility that identifies log statements based on regex
2. `log-placement-analyzer`: Analyzes the placement of log statements in a project given a list of source files.
3. `log-remover`: Removes log statement from a project
4. `log-prediction`: ML component for experimentation
5. `java-token-extractor`: Utility for tokens and method calls extraction

## Hungry for more?

Feel free to post a question in the [Q&A Section](https://github.com/jeandersonbc/predictive-method-logging/discussions/categories/q-a) in the [Discussions](https://github.com/jeandersonbc/predictive-method-logging/discussions) tab.

Please, keep the **Issues** for bugs/code-related concerns/etc.


