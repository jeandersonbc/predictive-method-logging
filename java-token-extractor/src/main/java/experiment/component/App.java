package experiment.component;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class App {

    public static void main(String[] args) throws IOException {
        Path inputPath = Paths.get(args[0]);
        Path outputPath = inputPath.getParent();

        // dirty code to read my input file "<fpath> <ftype>"
        Files.lines(inputPath)
                .filter(e -> e.split(" ")[1].equals("production-related"))
                .map(e -> new String[]{e.split(" ")[0]})
                .forEach(App::process);
    }

    public static void process(String[] sources) {
        ASTParser parser = ASTParser.newParser(AST.JLS13);
        Hashtable<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_13, options);
        parser.setCompilerOptions(options);

        FileASTRequestor executor = new FileASTRequestor() {
            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                // TODO write token extraction
            }
        };
        parser.createASTs(sources, null, null, executor, null);
    }
}
