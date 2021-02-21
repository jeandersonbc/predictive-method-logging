package experiment.component;


import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class MethodVisitorTest {

    @Test
    public void shouldVisitExpectedMethods() throws URISyntaxException {
        Path rootDir = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("examples")).toURI());
        Path targetFile = Paths.get(rootDir.toString(), "Sample.java");
        String[] sourceFilePaths = {targetFile.toString()};

        MethodVisitor methodVisitor = new MethodVisitor();

        ASTParser parser = JavaUtils.newParser(rootDir);
        parser.createASTs(sourceFilePaths, null, new String[0], new FileASTRequestor() {

            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                ast.accept(methodVisitor);
            }

        }, null);

        List<String> actualMethods = methodVisitor.visitedMethods();
        List<String> expectedMethods = Arrays.asList(
                "Sample::(initializer 1)",
                "Sample::(initializer 2)",
                "Sample::Sample/0",
                "Sample::Sample/2[Sample.Foo,int]",
                "Sample$Anonymous1::run/1[int]",
                "Sample::main/1[java.lang.String[]]",
                "Sample::process/1[List<java.lang.Object>]",
                "Sample$Foo::foo/0",
                "Sample$Foo$Anonymous2::run/1[int[]]",
                "Sample$Foo::bar/0",
                "Sample$Foo$Anonymous3::run/1[java.lang.String[]]"
        );
        Collections.sort(expectedMethods);
        Collections.sort(actualMethods);

        assertIterableEquals(expectedMethods, actualMethods);
    }

}
