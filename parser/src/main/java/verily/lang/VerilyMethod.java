package verily.lang;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

public class VerilyMethod {

    private String method;
    private List<VerilyType> formalParameters;
    private List<VerilyType> sessionBoundParameters;
    private List<VerilyType> nonSessionBoundParameters;

    public VerilyMethod(String method, List<VerilyType> formalParameters) {
        this.setMethod(method);
        this.setFormalParameters(formalParameters);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<VerilyType> getFormalParameters() {
        return formalParameters;
    }

    public void setFormalParameters(List<VerilyType> formalParameters) {
        this.formalParameters = formalParameters;
        this.sessionBoundParameters = null;
        this.nonSessionBoundParameters = null;
    }


    public List<VerilyType> getNonSessionBoundParameters() {

        if (nonSessionBoundParameters == null) {
            nonSessionBoundParameters = new LinkedList<VerilyType>();

            for (VerilyType t : getFormalParameters()) {
                if (t.isSessionBound() == false) {
                    nonSessionBoundParameters.add(t);
                }
            }
        }

        return nonSessionBoundParameters;
    }


    public List<VerilyType> getSessionBoundParameters() {

        if (sessionBoundParameters == null) {
            sessionBoundParameters = new LinkedList<VerilyType>();

            for (VerilyType t : getFormalParameters()) {
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
