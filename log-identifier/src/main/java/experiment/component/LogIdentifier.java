package nl.tudelft.serg;

public class LogIdentifier {

    static public boolean isLogStatement(String statement) {
        String line = statement.toLowerCase();
        return (line.matches(".*\\.(info|warn|debug|error)\\(.*")
                || line.matches(".*log(ger)?\\..*"));
    }
}
