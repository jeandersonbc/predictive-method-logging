package experiment.component;


import org.eclipse.jdt.core.dom.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodVisitor extends ASTVisitor {

    private final Set<MethodExtractedData> result;

    private final LinkedList<String> methodContext;
    private final LinkedList<String> classContext;
    private int anonymousClassContextCounter;
    private int staticContextCounter;

    public MethodVisitor() {
        result = new HashSet<>();
        classContext = new LinkedList<>();
        methodContext = new LinkedList<>();
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
        String id = classContext.peekLast() + "::" + methodContext.peekLast();

        IdentifierAndMethodExprVisitor visitor = new IdentifierAndMethodExprVisitor();
        node.accept(visitor);

        MethodExtractedData data = new MethodExtractedData();
        data.setMethodName(id);
        data.setMethodCalls(visitor.getMethodCallExpressions());
        data.setTokens(visitor.getIdentifiers());
        result.add(data);

        return super.visit(node);
    }

    @Override
    public void endVisit(Initializer node) {
        methodContext.removeLast();
        super.endVisit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        methodContext.addLast(JavaUtils.getMethodFullName(node));
        String id = classContext.peekLast() + "::" + methodContext.peekLast();

        IdentifierAndMethodExprVisitor visitor = new IdentifierAndMethodExprVisitor();
        node.accept(visitor);

        MethodExtractedData data = new MethodExtractedData();
        data.setMethodName(id);
        data.setMethodCalls(visitor.getMethodCallExpressions());
        data.setTokens(visitor.getIdentifiers());
        result.add(data);

        return super.visit(node);
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        methodContext.removeLast();
        super.endVisit(node);
    }

    public List<String> visitedMethods() {
        return this.result.stream().map(MethodExtractedData::getMethodName).collect(Collectors.toList());
    }

    public Set<MethodExtractedData> getResult() {
        return this.result;
    }
}
