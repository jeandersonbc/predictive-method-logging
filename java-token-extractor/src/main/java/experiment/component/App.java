package experiment.component;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {

    private static int progress = 0;

    public static void main(String[] args) throws IOException {

        final Path start = Paths.get(args[0]);

        ProjectVisitor projectVisitor = new ProjectVisitor();
        Files.walkFileTree(start, projectVisitor);

        String[] sources = projectVisitor.visitedFiles()
                .stream()
                .map(e -> e.toFile().toString())
                .toArray(String[]::new);

        final int totalFiles = sources.length;

        ASTParser parser = JavaUtils.newParser(start);
        try (ResultWriter writer = new ResultWriter("tokens.json")) {

            parser.createASTs(sources, null, new String[0], new FileASTRequestor() {

                @Override
                public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                    double perc = 100.0 * ++progress / totalFiles;
                    System.out.printf("File (%5.1f%%): %s%n", perc, sourceFilePath);

                    MethodVisitor methodVisitor = new MethodVisitor();
                    ast.accept(methodVisitor);

                    Path relativePath = start.relativize(Paths.get(sourceFilePath));
                    String formatted = String.format("./%s", relativePath.toString());
                    writer.register(formatted, methodVisitor.getResult());
                }
            }, null);
        }
    }

}
