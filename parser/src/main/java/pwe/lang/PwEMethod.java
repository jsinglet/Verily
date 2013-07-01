package pwe.lang;

import java.util.List;

public class PwEMethod {

    private String method;
    private List<PwEType> formalParameters;

    public PwEMethod(String method, List<PwEType> formalParameters) {
        this.setMethod(method);
        this.setFormalParameters(formalParameters);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<PwEType> getFormalParameters() {
        return formalParameters;
    }

    public void setFormalParameters(List<PwEType> formalParameters) {
        this.formalParameters = formalParameters;
    }
}
