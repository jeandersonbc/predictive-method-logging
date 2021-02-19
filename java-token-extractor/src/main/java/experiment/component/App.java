package experiment.component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {

    public static void main(String[] args) throws IOException {

        Path start = Paths.get(".", "java-token-extractor", "src", "test", "resources");

        ProjectVisitor projectVisitor = new ProjectVisitor();
        Files.walkFileTree(start, projectVisitor);

        String[] sources = projectVisitor.visitedFiles()
                .stream()
                .map(e -> e.toFile().toString())
                .toArray(String[]::new);
    }

}
