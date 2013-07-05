package pwe.lang;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

public class PwEMethod {

    private String method;
    private List<PwEType> formalParameters;
    private List<PwEType> sessionBoundParameters;
    private List<PwEType> nonSessionBoundParameters;

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
        this.sessionBoundParameters = null;
        this.nonSessionBoundParameters = null;
    }


    public List<PwEType> getNonSessionBoundParameters() {

        if (nonSessionBoundParameters == null) {
            nonSessionBoundParameters = new LinkedList<PwEType>();

            for (PwEType t : getFormalParameters()) {
                if (t.getType().startsWith("ReadableValue<") == false && t.getType().startsWith("WritableValue<") == false) {
                    nonSessionBoundParameters.add(t);
                }
            }
        }

        return nonSessionBoundParameters;
    }


    public List<PwEType> getSessionBoundParameters() {

        if (sessionBoundParameters == null) {
            sessionBoundParameters = new LinkedList<PwEType>();

            for (PwEType t : getFormalParameters()) {
                if (t.getType().startsWith("ReadableValue<") || t.getType().startsWith("WritableValue<")) {
                    sessionBoundParameters.add(t);
                }
            }
        }

        return sessionBoundParameters;
    }

    public boolean equals(Object o) {

        if (o instanceof PwEMethod) {
            PwEMethod that = (PwEMethod) o;
            boolean sameAddress = (this == that);
            boolean sameContents = that.getMethod().equals(this.getMethod()) && this.getFormalParameters().equals(that.getFormalParameters());

            return sameAddress || sameContents;
        }
        return false;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(method).append(formalParameters).hashCode();
    }


}
