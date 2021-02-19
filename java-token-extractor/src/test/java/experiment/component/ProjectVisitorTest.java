package experiment.component;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class ProjectVisitorTest {

    @Test
    void shouldFindJavaSources() throws IOException, URISyntaxException {
        Path root = Paths.get(getClass().getClassLoader().getResource("examples").toURI());
        ProjectVisitor projectVisitor = new ProjectVisitor();
        Files.walkFileTree(root, projectVisitor);

        List<String> expectedFiles = Arrays.asList("LambdaExpressions.java", "Sample.java");
        List<String> foundFiles = collectFileNames(projectVisitor.visitedFiles());

        Collections.sort(expectedFiles);
        Collections.sort(foundFiles);

        assertIterableEquals(expectedFiles, foundFiles);
    }

    private List<String> collectFileNames(List<Path> visitedFiles) {
        return visitedFiles.stream()
                .map(e -> e.getFileName().toFile().getName())
                .collect(Collectors.toList());
    }
}
