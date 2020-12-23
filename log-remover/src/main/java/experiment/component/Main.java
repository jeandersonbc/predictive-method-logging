package experiment.component;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        LogRemoval remover = new LogRemoval();
        String dir = args[0];

        String project = Paths.get(dir).getFileName().toString();
        System.out.println("Starting project " + project);
        // careful: it's gonna overwrite the files!

        long startTime = System.currentTimeMillis();

        // gets all the java files that need transforming
        List<Path> javaFiles = Files.walk(Paths.get(dir))
                .filter(Files::isRegularFile)
                .filter(x -> !x.toAbsolutePath().toString().contains(".git"))
                .filter(x -> x.toAbsolutePath().toString().toLowerCase().endsWith(".java"))
                .map(Path::toAbsolutePath)
                .collect(Collectors.toList());

        for (Path javaFile : javaFiles) {
            System.out.println(javaFile.toAbsolutePath());
            try {
                ParserConfiguration config = StaticJavaParser.getConfiguration();
                config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_14);
                config.setAttributeComments(false);
                CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(javaFile.toString()));
                String modifiedSourceCode = remover.removeLog(cu);
                PrintWriter pw = new PrintWriter(new FileOutputStream(javaFile.toString()), false);
                pw.print(modifiedSourceCode);
                pw.close();
            } catch (Exception e) {
                System.err.println("FAIL " + javaFile.toString());
                System.out.println("FAIL " + javaFile.toString());
                e.printStackTrace();
            }
        }
        System.out.println("Done project " + project + " in " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }
}
