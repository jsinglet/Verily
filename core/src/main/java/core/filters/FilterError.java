package core.filters;

import verily.lang.VerilyMethod;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class FilterError {

    private String context;
    private String reason;
    private VerilyMethod method;

    public FilterError(String context, String reason, VerilyMethod method){
        this.setContext(context);
        this.setReason(reason);
        this.setMethod(method);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public VerilyMethod getMethod() {
        return method;
    }

    public void setMethod(VerilyMethod method) {
        this.method = method;
    }
}
