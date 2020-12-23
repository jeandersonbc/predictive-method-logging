package experiment.component;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
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
                    String rawExpr = getRawLogStatementFrom(exprStmt);
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
                        boolean wasRemovedByGuard = removeWithLogGuard(exprStmt);

                        if (!wasRemovedByGuard) {
                            boolean result = currentNode.remove();
                            // handles if-else expressions if (...) ... ;
                            while (!result && currentNode.getParentNode().isPresent()) {
                                currentNode = currentNode.getParentNode().get();
                                result = currentNode.remove();
                            }

                            if (!result) {
                                System.out.println("- error in call " + exprStmt);
                            }
                        }

                    }
                }
                return super.visit(n, arg);

            }

            private String getRawLogStatementFrom(ExpressionStmt exprStmt) {
                // In some occasions, the log statement is mixed with comments and makes JavaParser crazy
                String[] logStatementMixedWithComments = exprStmt.toString().trim().split("\n");
                return logStatementMixedWithComments[logStatementMixedWithComments.length - 1];
            }

            private boolean removeWithLogGuard(ExpressionStmt exprStmt) {
                boolean wasRemovedByGuard = false;

                // check for guard
                Node parentNode = exprStmt;
                IfStmt guard = null;
                BlockStmt body = null;

                int maxDepthForGuard = 2; // if + block
                for (int i = 0; i < maxDepthForGuard && parentNode.getParentNode().isPresent(); i++) {
                    parentNode = parentNode.getParentNode().get();
                    if (parentNode instanceof BlockStmt) {
                        body = (BlockStmt) parentNode;
                    }
                    if (parentNode instanceof IfStmt) {
                        guard = (IfStmt) parentNode;
                    }
                }
                if (guard != null && body != null) {
                    boolean isIfAlone = !guard.hasCascadingIfStmt()
                            && !guard.hasElseBlock()
                            && !guard.hasElseBranch()
                            && guard.getParentNode().isPresent()
                            && guard.getParentNode().get() instanceof BlockStmt;

                    boolean hasOnlyLogStatements = true;
                    for (Node n : body.getChildNodes()) {
                        if (n instanceof ExpressionStmt) {
                            String s = getRawLogStatementFrom((ExpressionStmt) n);
                            if (!LogIdentifier.isLogStatement(s)) {
                                hasOnlyLogStatements = false;
                                break;
                            }
                        } else {
                            hasOnlyLogStatements = false;
                            break;
                        }
                    }
                    if (isIfAlone && hasOnlyLogStatements) {
                        wasRemovedByGuard = guard.remove();
                    }
                }
                return wasRemovedByGuard;
            }
        }, null);
        return cu.toString();
    }
}
