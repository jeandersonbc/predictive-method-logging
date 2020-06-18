Predictive Method Logging
-----
[![Build Status](https://travis-ci.com/jeandersonbc/predictive-method-logging.svg?token=VmF136TKUqZzBdun2K3C&branch=master)](https://travis-ci.com/jeandersonbc/predictive-method-logging)

Learning whether a method should be logged or not based on code metrics.

### Minimum requirements:
* Bash environment to run scripts
* Java >= 8.0
* Python >= 3.6 with pip available

**Important:** _The subject selection step in our experiments relies on a `R` script with the `dplyr` package available. This is only required if you are interested in replicating our study or trying different selection criteria._

### Getting Started

1. Install the Python dependencies: `pip3 install -r requirements`
    * Some Python scripts depends on those dependencies (e.g., Pandas and Numpy) to analyze intermediate data during experimentation
2. Build the project components: `./gradlew deploy-aux-tools`
3. Get the initial list of Apache projects: `./gradlew fetch-apache-projects`
    * This will download 64 Apache projects from [a CSV file](https://github.com/jeandersonbc/predictive-method-logging/blob/master/apache-projects.csv) into a `apache-downloads` dir. Also, it will generate a `apache-projects` dir with scripts that exports the absolute path and revision of a given project.

By now, you should be able have some fun. Try to process the project Apache Commons BeansUtils:

**TODO**

### Components

1. `log-identifier`: Cross-project utility that identifies log statements based on regex
2. `log-placement-analyzer`: Analyzes the placement of log statements in a project given a list of source files.
3. `log-remover`: Removes log statement from a project
4. `log-prediction`: ML component for experimentation
