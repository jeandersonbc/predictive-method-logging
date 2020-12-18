package nl.tudelft.serg;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import java.util.Optional;

public class LogRemoval {

    public String removeLog(CompilationUnit cu) {
        cu.accept(new ModifierVisitor<Void>() {

            @Override
            public Visitable visit(final MethodCallExpr n, Void arg) {
                Optional<Node> parent = n.getParentNode();
                if (parent.isPresent() && parent.get() instanceof ExpressionStmt) {
                    ExpressionStmt exprStmt = (ExpressionStmt) parent.get();
                    String[] split = exprStmt.toString().trim().split("\n");
                    String rawExpr = split[split.length - 1];
                    if (LogIdentifier.isLogStatement(rawExpr)) {
                        /*
                         * We then find the first statement we can remove.
                         * We try first the log line itself, e.g., log.info(...)
                         * If it fails, that's because that line can't be removed without
                         * making the code to stop compiling. Imagine a if(a) log.info(...).
                         * In this case, we then visit the parent node and delete it.
                         * In the example above, that makes sense as the if is only there
                         * because of the log.
                         * However, there are some caveats:
                         * an `if(a) log.info(...);` would be completely deleted, but an
                         * `if(a) { log.info(...);` would become an `if(a) {}`.
                         * In future work, we should try to improve our transformation algorithm.
                         */
                        Node currentNode = exprStmt;
                        boolean result = currentNode.remove();
                        while (!result && currentNode.getParentNode().isPresent()) {
                            currentNode = currentNode.getParentNode().get();
                            result = currentNode.remove();
                        }
                        if (!result) {
                            System.out.println("- error in call " + exprStmt);
                        }

                    }
                }
                return super.visit(n, arg);

            }
        }, null);
        return cu.toString();
    }
}
