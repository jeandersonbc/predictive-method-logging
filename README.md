Predictive Method Logging
-----
[![Build Status](https://travis-ci.com/jeandersonbc/predictive-method-logging.svg?token=VmF136TKUqZzBdun2K3C&branch=master)](https://travis-ci.com/jeandersonbc/predictive-method-logging)

Learning whether a method should be logged or not based on code metrics.

### Components

1. `log-identifier`: Cross-project utility that identifies log statements based on regex
2. `log-placement-analyzer`: Analyzes the placement of log statements in a project given a list of source files.
3. `log-remover`: Removes log statement from a project
