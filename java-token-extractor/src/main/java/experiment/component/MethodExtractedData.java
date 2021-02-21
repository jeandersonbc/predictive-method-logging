package experiment.component;

import java.util.List;
import java.util.Objects;

public class MethodExtractedData {

    private List<String> tokens;
    private List<String> methodCalls;
    private String methodName;

    public List<String> getMethodCalls() {
        return methodCalls;
    }

    public void setMethodCalls(List<String> methodCalls) {
        this.methodCalls = methodCalls;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodExtractedData that = (MethodExtractedData) o;
        return Objects.equals(getTokens(), that.getTokens()) && Objects.equals(getMethodCalls(), that.getMethodCalls()) && Objects.equals(getMethodName(), that.getMethodName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTokens(), getMethodCalls(), getMethodName());
    }
}