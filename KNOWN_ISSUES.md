### Log removal component

1. **Unable to remove ~1/5 of expected log statements:**

```
~/workspace/predictive-method-logging
1:05 jeanderson@mb-jeanderson /Users/jeanderson/workspace/predictive-method-logging
% ./pipeline-analysis.sh subjects/adyen.sh
~/workspace/adyen-main ~/workspace/predictive-method-logging
Looking for java sources
Found 40715 java files
  57 build-related
34526 production-related
6132 test-related
Running log placement analysis
->> 51004 log statements (production only) <<-
~/workspace/predictive-method-logging

% ./pipeline-analysis.sh subjects/adyen-nolog.sh
~/workspace/predictive-method-logging/transformed-sources/adyen-main-nolog ~/workspace/predictive-method-logging
Looking for java sources
Found 34526 java files
34526 production-related
Running log placement analysis
->> 9698 log statements (production only) <<-
```
