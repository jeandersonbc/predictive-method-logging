package experiment.component;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LambdaExpressionsTest {

    private static final LogPlacementAnalyzer analyzer = new LogPlacementAnalyzer();
    private static final String FILE_NAME = "/LambdaExpressions.java";

    @BeforeAll
    public static void run() {
        String resourcePath = LambdaExpressionsTest.class.getResource(FILE_NAME).getPath();
        String[] sources = new String[]{resourcePath};

        ASTParser parser = ASTParser.newParser(AST.JLS13);
        Hashtable<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_13, options);
        parser.setCompilerOptions(options);
        FileASTRequestor executor = new FileASTRequestor() {

            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                ast.accept(analyzer);
            }
        };
        parser.createASTs(sources, null, null, executor, null);
    }

    @Test
    void testAnalyzerCounting() {
        assertEquals(1, analyzer.countingVisitedMethods());
        assertEquals(2, analyzer.visitedLogStatements().size());
        assertEquals(1, analyzer.countingLoggedMethods());
    }


}
