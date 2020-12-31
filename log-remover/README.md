Log-Remover
-----------

Removes log statements from a project.
Powered by JavaParser and the log-identifier component.

We consider as a log-guard any if-statement (or if-expression) that contains only a log call.
Note that it does not necessarily means only ``if (logger.isWarningEnabled()) { ... }`` but any
condition that fits our description, i.e., log call guarded by condition.

For further details about how it operates, check the tests and related resources (fixtures)
