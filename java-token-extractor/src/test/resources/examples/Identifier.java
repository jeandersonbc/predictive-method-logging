/**
 * The following snippet is not semantically correct and would not compile.
 * However, it is syntactly correct and should be parseable.
 */
class Sample {

    public static void main(String[] args) {
        System.out.println("Starting...");
        Sample s = new Sample();
        s.process();
    }

}