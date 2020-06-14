/**
 * The following snippet is not semantically correct and would not compile.
 * However, it is syntactly correct and should be parseable.
 */
class Sample {

    // Method 1 / Logged 1
    static {
        LOG.info("Starting static context...");
    }

    // Method 2 / Logged 2
    static {
        LOG.info("Starting other static context...");
    }

    // Method 3 / Logged 3
    public Sample() {
        LOG.info("hello from constructor...");
    }

    // Method 4 / Logged 4
    public Sample(int a, int b) {
        LOG.info("hello from other constructor...");
        doSomething(new Interface() {
            // Method 5 / Logged 5
            void run(int a) {
                LOG.info("anonymous class...");
            }
        });
    }

    // Method 6
    public static void main(String[] args) {
        System.out.println("Starting...");
        Sample s = new Sample();
        s.process();
    }

    // Method 7 / Logged 6
    void process() {
        log.info("Started processing");

        if (1 < 3) {
            assert "some people do use assertions and it can mess JDT it's not configured properly!".isEmpty()
            log.error("Unable to meet important condition");
        } else if (40 > 2) {
            log.warn("Unable to meet some less important condition");
        } else {
            log.info("All conditions are fine...")
        }

        try {
            // ...
            if (true) {
                log.info("Inside a if that is inside a try statement");
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("Oh Lord, have mercy!")
                    .with("keyA", varA)
                    .with("keyB", varB);
        } finally {
            log.info("Chaos handled, back to execution");
        }
        log.info("Ended processing");

        MyCollection.forEach(e -> {
            log.info("processing items in a lambda expression");
            e.doSomething();
        })
    }

    static class Foo {

        // Method 8 / Logged 7
        static void foo() {
            logger.warn("From inner class");
            doSomething(new Interface() {

                // Method 9 / Logged 8
                void run(int a) {
                    LOG.info("anonymous class...");
                }
            });
        }

        // Method 10
        static void bar() {
            call();
            doSomething(new Interface() {

                // Method 11 / Logged 9
                void run(int a) {
                    LOG.info("anonymous class...");
                }
            });
        }
    }

}