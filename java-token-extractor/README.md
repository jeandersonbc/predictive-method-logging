This is a utility tool to extract textual features from methods.
Given the root directory of a java project, the tool outputs a json file with the following schema:


* A java project contains a list of source files
* A source file contains a `fileName` and `data` extracted from methods
* A method contains a list of `tokens`, a list of `methodCalls`, and a `methodName` (qualified)

```
[
  {
      "fileName": "...",
      "data": [
         {
            "tokens": [...],
            "methodCalls": [...],
            "methodName: "Class::fooBar/1(int)"
         },
         ...
      ]
  },
  ...
]
```
