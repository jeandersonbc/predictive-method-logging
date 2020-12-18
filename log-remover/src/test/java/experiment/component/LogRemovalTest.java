package nl.tudelft.serg;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LogRemovalTest {

    private LogRemoval remover;

    @BeforeEach
    void setUp() {
        this.remover = new LogRemoval();
    }

    // some simple examples of log4j usage
    @Test
    void testA() {
        CompilationUnit cu = loadFixture("/fixture/A.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("log.info");
        assertThat(sc).doesNotContain("log.error");
    }

    @Test
    void testClearnerUtil() {
        CompilationUnit cu = loadFixture("/fixture/CleanerUtil.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("LOG.info");
        assertThat(sc).doesNotContain("LOG.error");
        assertThat(sc).doesNotContain("LOG.debug");
        assertThat(sc).doesNotContain("LOG.warn");
    }

    @Test
    void testXMLContentLoader() {
        CompilationUnit cu = loadFixture("/fixture/XMLContentLoader.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("LOG.info");
        assertThat(sc).doesNotContain("LOG.error");
        assertThat(sc).doesNotContain("LOG.debug");
        assertThat(sc).doesNotContain("LOG.warn");
    }

    @Test
    void testKeymasterConfParamLoader() {
        CompilationUnit cu = loadFixture("/fixture/KeymasterConfParamLoader.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("LOG.info");
        assertThat(sc).doesNotContain("LOG.warn");
        assertThat(sc).doesNotContain("LOG.error");
        assertThat(sc).doesNotContain("LOG.debug");
    }

    @Test
    void testArchivaCli() {
        CompilationUnit cu = loadFixture("/fixture/ArchivaCli.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("LOGGER.info");
    }

    // using this.log
    @Test
    void testB() {
        CompilationUnit cu = loadFixture("/fixture/B.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("log.info");
    }

    // if without opening a block
    @Test
    void testC() {
        CompilationUnit cu = loadFixture("/fixture/C.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("log.info");

        // it removes the entire if
        assertThat(sc).doesNotContain("if(a>1)");
        assertThat(sc).doesNotContain("if(b>1)");
        assertThat(sc).doesNotContain("else");
    }

    // log with the fluent interface
    @Test
    void testD() {
        CompilationUnit cu = loadFixture("/fixture/D.java");

        String sc = remover.removeLog(cu);
        assertThat(sc).doesNotContain("log.info");
        assertThat(sc).doesNotContain("fluent");

    }

    private CompilationUnit loadFixture(String fixture) {
        try {
            ParserConfiguration config = StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_14);
            StaticJavaParser.setConfiguration(config);
            return StaticJavaParser.parse(new FileInputStream(getClass().getResource(fixture).getFile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
