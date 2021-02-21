package experiment.component;

import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class App {

    public static void main(String[] args) throws IOException {

        Path start = Paths.get(args[0]);

        ProjectVisitor projectVisitor = new ProjectVisitor();
        Files.walkFileTree(start, projectVisitor);

        String[] sources = projectVisitor.visitedFiles()
                .stream()
                .map(e -> e.toFile().toString())
                .toArray(String[]::new);

        ASTParser parser = JavaUtils.newParser(start);
        System.out.println(Arrays.toString(sources));
        try (ResultWriter writer = new ResultWriter("tokens.json")) {

            parser.createASTs(sources, null, new String[0], new FileASTRequestor() {

                @Override
                public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                    System.out.println("Processing: " + sourceFilePath);
                    MethodVisitor methodVisitor = new MethodVisitor();
                    ast.accept(methodVisitor);
                    writer.register(sourceFilePath, methodVisitor.getResult());
                }
            }, null);
        }
    }

}
