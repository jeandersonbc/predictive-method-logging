package experiment.component;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class JavaUtils {

    public static ASTParser newParser(Path rootDir) {
        String[] srcDir = new String[]{rootDir.toFile().getAbsolutePath()};
        ASTParser parser = ASTParser.newParser(AST.JLS13);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        Hashtable<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_13, options);
        parser.setCompilerOptions(options);
        parser.setEnvironment(null, srcDir, null, true);
        return parser;
    }

    public static String getMethodFullName(MethodDeclaration node) {
        if (node.resolveBinding() != null) {
            return getMethodFullName(node.resolveBinding());
        }
        String methodName = node.getName().getFullyQualifiedName();
        return methodName + "/" + getMethodSignature(node);
    }

    public static String getMethodFullName(IMethodBinding binding) {
        String methodName = binding.getName();
        return methodName + "/" + getMethodSignature(binding);
    }

    public static String getMethodSignature(IMethodBinding node) {
        int parameterCount = node.getParameterTypes() == null ? 0 : node.getParameterTypes().length;
        List<String> parameterTypes = new ArrayList<>();

        if (parameterCount > 0) {
            for (ITypeBinding binding : node.getParameterTypes()) {

                String v = binding.getQualifiedName();

                parameterTypes.add(v);
            }
        }
        return formatSignature(parameterTypes);
    }

    //Get the signature of a method with parameter count and types, e.g. 1[int]
    public static String getMethodSignature(MethodDeclaration node) {
        int parameterCount = node.parameters() == null ? 0 : node.parameters().size();
        List<String> parameterTypes = new ArrayList<>();

        if (parameterCount > 0) {
            for (Object p0 : node.parameters()) {
                SingleVariableDeclaration parameter = (SingleVariableDeclaration) p0;

                ITypeBinding binding = parameter.getType().resolveBinding();

                String v;
                if (binding == null || binding.isRecovered())
                    v = parameter.getType().toString();
                else
                    v = binding.getQualifiedName();

                if (parameter.isVarargs()) v += "[]";

                parameterTypes.add(v);
            }
        }

        return formatSignature(parameterTypes);
    }

    private static String formatSignature(List<String> parameters) {
        int parameterCount = parameters.size();
        return String.format("%d%s%s%s",
                parameterCount,
                (parameterCount > 0 ? "[" : ""),
                (parameterCount > 0 ? String.join(",", parameters) : ""),
                (parameterCount > 0 ? "]" : "")
        );
    }
}
