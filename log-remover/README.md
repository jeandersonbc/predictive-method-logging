Log-Remover
-----------

Removes log statements from a project.
Powered by JavaParser and the log-identifier component.

### Known Issues

Ideally [this code](https://github.com/jeandersonbc/predictive-method-logging/blob/master/log-remover/src/test/resources/fixture/RecursiveRemovalTest2.java) would be transformed to [this other code](https://github.com/jeandersonbc/predictive-method-logging/blob/master/log-remover/src/test/resources/expected/RecursiveRemovalTest2.java).
There is [a test case disabled](https://github.com/jeandersonbc/predictive-method-logging/blob/794c694753fcd9dacd59f732612edae15fa8b232/log-remover/src/test/java/nl/tudelft/serg/RecursiveRemovalTest.java#L30) that covers this case.
While it seems simple to just check whether the enclosing block is empty and remove it, it gets
trickier if this is a if-else statement with lots of "if/if else" cases.
To keep it simple, we just remove the identified log statements.

Contributions are welcome! You can even import just this component in your favorite IDE along with the log-identifier component.

### Acknowledgments

Initial implementation was developed by @mauricioaniche.
I reused his initial implementation to integrate it as a component to this project.
I converted it to Gradle, updated the log identification code to be consistent with CK and the log-analyzer component, and added some test cases.
Unfortunately, I was not able to keep the original commits as I started this repository as a fresh new repository.
