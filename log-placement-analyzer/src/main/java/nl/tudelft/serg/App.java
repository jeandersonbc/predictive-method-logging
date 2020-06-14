package nl.tudelft.serg;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class App {

    private static final String[] HEADER = "file,total_log_stmts,class,method,enclosing_context".split(",");
    private static CSVPrinter csv;
    private static PrintWriter statements;

    private static int globalMethodCounter;
    private static int globalLoggedMethodCounter;

    public static void main(String[] args) throws IOException {
        Path inputPath = Paths.get(args[0]);
        Path outputPath = inputPath.getParent();

        Path outputStatements = Paths.get(outputPath.toString(), "log-statements.txt");
        BufferedWriter stmtsBw = Files.newBufferedWriter(outputStatements);
        statements = new PrintWriter(stmtsBw);

        Path outputCsv = Paths.get(outputPath.toString(), "log-placement.csv");
        BufferedWriter bw = Files.newBufferedWriter(outputCsv);
        csv = new CSVPrinter(bw, CSVFormat.DEFAULT.withHeader(HEADER));

        System.out.printf("input file: %s\noutput csv: %s\noutput txt: %s\n", inputPath, outputCsv, outputStatements);

        // dirty code to read my input file "<fpath> <ftype>"
        Files.lines(inputPath)
                .filter(e -> e.split(" ")[1].equals("production-related"))
                .map(e -> new String[]{e.split(" ")[0]})
                .forEach(App::process);

        System.out.println(
                String.format(Locale.US, "\nmethods %d logged %d ratio %.1f %%",
                        globalMethodCounter,
                        globalLoggedMethodCounter,
                        ((float) globalLoggedMethodCounter) / globalMethodCounter * 100
                )
        );
        statements.flush();
        csv.flush();
    }

    public static void process(String[] sources) {
        ASTParser parser = ASTParser.newParser(AST.JLS13);
        Hashtable<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_13, options);
        parser.setCompilerOptions(options);

        FileASTRequestor executor = new FileASTRequestor() {
            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                LogPlacementAnalyzer visitor = new LogPlacementAnalyzer();
                ast.accept(visitor);
                List<LogPlacementAnalyzer.VisitedLogStatement> data = visitor.visitedLogStatements();
                System.out.printf("%d ", data.size());

                statements.println(sourceFilePath);
                data.forEach(e -> {
                    try {
                        csv.printRecord(sourceFilePath, data.size(),
                                e.clazz, e.method, e.enclosingBlock);
                        statements.println(e.logStatement);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
                globalLoggedMethodCounter += visitor.countingLoggedMethods();
                globalMethodCounter += visitor.countingVisitedMethods();
            }
        };
        parser.createASTs(sources, null, null, executor, null);
    }
}
