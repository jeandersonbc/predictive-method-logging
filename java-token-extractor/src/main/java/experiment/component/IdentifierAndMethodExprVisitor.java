package experiment.component;

import org.eclipse.jdt.core.dom.*;

import java.util.LinkedList;
import java.util.List;

public class IdentifierAndMethodExprVisitor extends ASTVisitor {

    private final List<String> identifiers;
    private final List<String> methodCallExpressions;

    public IdentifierAndMethodExprVisitor() {
        this.identifiers = new LinkedList<>();
        this.methodCallExpressions = new LinkedList<>();
    }

    @Override
    public boolean visit(SimpleName node) {
        identifiers.add(node.getIdentifier());
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        ASTNode parent = node.getParent();

        if (parent instanceof ExpressionStatement) {
            Expression expr = ((ExpressionStatement) parent).getExpression();

            // Ignores the arguments
            String exprStr = expr.toString();
            exprStr = exprStr.substring(0, exprStr.indexOf("("));

            methodCallExpressions.add(exprStr);
        }
        return super.visit(node);
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public List<String> getMethodCallExpressions() {
        return methodCallExpressions;
    }
}
