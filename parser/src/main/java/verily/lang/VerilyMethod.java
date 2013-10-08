package verily.lang;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

public class VerilyMethod {

    private String method;
    private List<PwEType> formalParameters;
    private List<PwEType> sessionBoundParameters;
    private List<PwEType> nonSessionBoundParameters;

    public VerilyMethod(String method, List<PwEType> formalParameters) {
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
                if (t.isSessionBound() == false) {
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
                if (t.isSessionBound()) {
                    sessionBoundParameters.add(t);
                }
            }
        }

        return sessionBoundParameters;
    }

    public boolean equals(Object o) {

        if (o instanceof VerilyMethod) {
            VerilyMethod that = (VerilyMethod) o;
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
