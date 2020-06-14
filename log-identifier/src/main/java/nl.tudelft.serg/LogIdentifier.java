package nl.tudelft.serg;

public class LogIdentifier {

    //    static {
//        logMethods.add("info");
//        logMethods.add("debug");
//        logMethods.add("warn");
//        logMethods.add("error");
//        logMethods.add("fatal");
//        logMethods.add("trace");
//
//        logClasses.add("log");
//        logClasses.add("logger");
//    }

    static public boolean isLogStatement(String statement) {
        String line = statement.toLowerCase();
        return (line.matches(".*\\.(info|warn|debug|error)\\(.*")
                || line.matches(".*log(ger)?\\..*"));
    }
}
