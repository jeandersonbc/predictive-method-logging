package experiment.component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class ProjectVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> files;

    public ProjectVisitor() {
        this.files = new LinkedList<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toFile().getName().endsWith(".java")) {
            this.files.add(file);
        }
        return super.visitFile(file, attrs);
    }

    public List<Path> visitedFiles() {
        return this.files;
    }
}
