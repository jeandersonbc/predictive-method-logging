package experiment.component;

import org.eclipse.jdt.core.dom.*;

import java.util.LinkedList;
import java.util.List;

public class LogPlacementAnalyzer extends ASTVisitor {

    private final LinkedList<String> classContext;
    private final LinkedList<String> methodContext;
    private final LinkedList<Boolean> isLoggedContext;
    private final LinkedList<VisitedLogStatement> result;

    private int staticContextCounter;
    private int anonymousClassContextCounter;
    private int methodCounter;
    private int loggedMethodCounter;

    public LogPlacementAnalyzer() {
        classContext = new LinkedList<>();
        methodContext = new LinkedList<>();
        result = new LinkedList<>();
        isLoggedContext = new LinkedList<>();
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        String name = node.getName().toString();
        if (node.isMemberTypeDeclaration()) {
            name = String.format("%s$%s", classContext.peekLast(), name);
        }
        classContext.addLast(name);
        return super.visit(node);
    }


    @Override
    public void endVisit(TypeDeclaration node) {
        classContext.removeLast();
        anonymousClassContextCounter = 0;
        super.endVisit(node);
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        String name = node.getName().toString();
        if (node.isMemberTypeDeclaration()) {
            name = String.format("%s$%s", classContext.peekLast(), name);
        }
        classContext.addLast(name);
        return super.visit(node);
    }

    @Override
    public void endVisit(EnumDeclaration node) {
        classContext.removeLast();
        anonymousClassContextCounter = 0;
        super.endVisit(node);
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        String name = String.format("%s$%s%d", classContext.peekLast(), "Anonymous", ++anonymousClassContextCounter);
        classContext.addLast(name);
        return super.visit(node);
    }

    @Override
    public void endVisit(AnonymousClassDeclaration node) {
        classContext.removeLast();
        super.endVisit(node);
    }

    @Override
    public boolean visit(Initializer node) {
        methodContext.addLast(String.format("(initializer %d)", ++staticContextCounter));
        methodCounter++;
        isLoggedContext.addLast(false);
        return super.visit(node);
    }

    @Override
    public void endVisit(Initializer node) {
        methodContext.removeLast();
        if (isLoggedContext.size() > 0 && isLoggedContext.peekLast())
            loggedMethodCounter++;
        isLoggedContext.removeLast();
        super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        String methodName = node.getName().getFullyQualifiedName() + "(";
        List<String> params = new LinkedList<>();
        for (Object e : node.parameters()) {
            if (e instanceof SingleVariableDeclaration) {
                SingleVariableDeclaration paramDeclaration = (SingleVariableDeclaration) e;
                Type type = paramDeclaration.getType();
                String typeName = type.toString();
                if (paramDeclaration.isVarargs()) {
                    typeName += "[]";
                }
                params.add(typeName);
            }
        }
        methodName += String.join(", ", params) + ")";

        methodContext.addLast(methodName);
        methodCounter++;
        isLoggedContext.addLast(false);
        return super.visit(node);
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        methodContext.removeLast();
        if (isLoggedContext.size() > 0 && isLoggedContext.peekLast())
            loggedMethodCounter++;
        isLoggedContext.removeLast();
        super.endVisit(node);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        ASTNode parent = node.getParent();

        // Working at Expression statement level allows to handle fluent API without duplicate work
        // as in a fluent api call, there is a chain of method invocations.
        if (parent instanceof ExpressionStatement) {
            ExpressionStatement expr = (ExpressionStatement) parent;
            String rawExpr = expr.toString().trim();
            if (LogIdentifier.isLogStatement(rawExpr)) {

                // TODO Known Issue
                // This is a lambda expression outside a method body (e.g., parameter to some constructor)
                // We skip this...
                if (isLoggedContext.isEmpty()) {
                    return super.visit(node);
                }

                // Update log context from current method
                if (!isLoggedContext.peekLast()) {
                    isLoggedContext.removeLast();
                    isLoggedContext.addLast(true);
                }
                ASTNode enclosingBlock = parent.getParent();
                if (enclosingBlock.getNodeType() == ASTNode.BLOCK) {
                    enclosingBlock = enclosingBlock.getParent();
                }
                int enclosingBlockNodeType = enclosingBlock.getNodeType();
                String enclosingBlockName;
                switch (enclosingBlockNodeType) {
                    case ASTNode.LABELED_STATEMENT:
                        enclosingBlockName = "labeled_statement";
                        break;
                    case ASTNode.SYNCHRONIZED_STATEMENT:
                        enclosingBlockName = "synchronized_statement";
                        break;
                    case ASTNode.ENHANCED_FOR_STATEMENT:
                        enclosingBlockName = "enhanced_for_statement";
                        break;
                    case ASTNode.LAMBDA_EXPRESSION:
                        enclosingBlockName = "lambda_expr";
                        break;
                    case ASTNode.WHILE_STATEMENT:
                        enclosingBlockName = "while_statement";
                        break;
                    case ASTNode.FOR_STATEMENT:
                        enclosingBlockName = "for_statement";
                        break;
                    case ASTNode.DO_STATEMENT:
                        enclosingBlockName = "do_statement";
                        break;
                    case ASTNode.IF_STATEMENT:
                        enclosingBlockName = "ifelse_statement";
                        break;
                    case ASTNode.INITIALIZER:
                        enclosingBlockName = "static_initializer";
                        break;
                    case ASTNode.METHOD_DECLARATION:
                        enclosingBlockName = "method_declaration";
                        break;
                    case ASTNode.CATCH_CLAUSE:
                        enclosingBlockName = "catch_clause";
                        break;
                    case ASTNode.TRY_STATEMENT:
                        enclosingBlockName = "try_statement";
                        break;
                    case ASTNode.SWITCH_STATEMENT:
                        enclosingBlockName = "switch_statement";
                        break;
                    default:
                        enclosingBlockName = String.valueOf(enclosingBlockNodeType);
                        break;
                }
                result.add(new VisitedLogStatement(rawExpr, enclosingBlockName, methodContext.peekLast(), classContext.peekLast()));
            }
        }
        return super.visit(node);
    }

    public List<VisitedLogStatement> visitedLogStatements() {
        return this.result;
    }

    public int countingVisitedMethods() {
        return this.methodCounter;
    }

    public int countingLoggedMethods() {
        return this.loggedMethodCounter;
    }

    static class VisitedLogStatement {
        final String logStatement,
                enclosingBlock,
                method, clazz;

        public VisitedLogStatement(String logStatement, String enclosingBlock, String method, String clazz) {
            this.logStatement = logStatement;
            this.enclosingBlock = enclosingBlock;
            this.method = method;
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return "{" +
                    "logStatement='" + logStatement + '\'' +
                    ", enclosingBlock='" + enclosingBlock + '\'' +
                    ", method='" + method + '\'' +
                    ", class='" + clazz + '\'' +
                    '}';
        }
    }
}
