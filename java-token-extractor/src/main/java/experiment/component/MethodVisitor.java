package experiment.component;


import org.eclipse.jdt.core.dom.*;

import java.util.LinkedList;
import java.util.List;

public class MethodVisitor extends ASTVisitor {

    private final List<String> visitedMethods;
    private final LinkedList<String> methodContext;
    private final LinkedList<String> classContext;
    private int anonymousClassContextCounter;
    private int staticContextCounter;

    public MethodVisitor() {
        visitedMethods = new LinkedList<>();
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
        return super.visit(node);
    }

    @Override
    public void endVisit(Initializer node) {
        visitedMethods.add(classContext.peekLast() + "::" + methodContext.removeLast());
        super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        methodContext.addLast(JavaUtils.getMethodFullName(node));
        return super.visit(node);
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        visitedMethods.add(classContext.peekLast() + "::" + methodContext.removeLast());
        super.endVisit(node);
    }

    public List<String> visitedMethods() {
        return this.visitedMethods;
    }

}
