package experiment.component;

import org.eclipse.jdt.core.dom.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class IdentifierAndMethodExprVisitorTest {

    private static final IdentifierAndMethodExprVisitor VISITOR = new IdentifierAndMethodExprVisitor();

    @BeforeAll
    public static void beforeAll() throws URISyntaxException {
        Path rootDir = Paths.get(Objects.requireNonNull(IdentifierAndMethodExprVisitorTest.class.getClassLoader().getResource("examples")).toURI());
        Path targetFile = Paths.get(rootDir.toString(), "Identifier.java");
        String[] sourceFilePaths = {targetFile.toString()};

        ASTParser parser = JavaUtils.newParser(rootDir);
        parser.createASTs(sourceFilePaths, null, new String[0], new FileASTRequestor() {

            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                ast.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(MethodDeclaration node) {
                        System.out.println(node);
                        node.accept(VISITOR);
                        return super.visit(node);
                    }
                });
            }

        }, null);

    }

    @Test
    public void shouldCollectExpectedTokens() {
        List<String> expectedTokens = Arrays.asList(
                "main", "String", "args",
                "System", "out", "println",
                "Sample", "s", "Sample",
                "s", "process");

        assertIterableEquals(expectedTokens, VISITOR.getIdentifiers());
    }

    @Test
    public void shouldCollectExpectedMethodCalls() {
        List<String> expectedTokens = Arrays.asList("System.out.println", "s.process");
        assertIterableEquals(expectedTokens, VISITOR.getMethodCallExpressions());
    }

}
