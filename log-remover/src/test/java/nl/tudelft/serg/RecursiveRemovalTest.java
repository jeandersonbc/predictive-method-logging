package nl.tudelft.serg;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RecursiveRemovalTest {
    private LogRemoval remover;

    @BeforeEach
    public void setup() {
        remover = new LogRemoval();
    }

    @Test
    void testCase1() {
        CompilationUnit cu = loadFixture("/fixture/RecursiveRemovalTest.java");
        String actual = remover.removeLog(cu);
        CompilationUnit expected = loadFixture("/expected/RecursiveRemovalTest.java");
        assertThat(actual).isEqualTo(expected.toString());
    }

    @Disabled("Not supported")
    @Test
    void testCase2() {
        CompilationUnit cu = loadFixture("/fixture/RecursiveRemovalTest2.java");
        String actual = remover.removeLog(cu);
        CompilationUnit expected = loadFixture("/expected/RecursiveRemovalTest2.java");
        assertThat(actual).isEqualTo(expected.toString());
    }

    @Test
    void testCase3() {
        CompilationUnit cu = loadFixture("/fixture/RecursiveRemovalTest3.java");
        String actual = remover.removeLog(cu);
        CompilationUnit expected = loadFixture("/expected/RecursiveRemovalTest3.java");
        assertThat(actual).isEqualTo(expected.toString());
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
