package core;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verily.lang.util.MRRTableSet;

public abstract class VerilyFilter {

    protected final Logger logger = LoggerFactory.getLogger(VerilyFilter.class);
    protected String filterName;

    public VerilyFilter(String filterName){
        this.filterName = filterName;
    }
    public String getFilterName(){
        return filterName;
    }

    public enum VerilyFilterAction {
        OK,         // OK means we did some processing and it worked.
        STOP,       // STOP means we should stop filter processing
        ERROR,      // ERROR means we tried to process a request, and something failed.
        CONTINUE;   // CONTINUE means we either a) won't process a request or don't need to or
                    // b) we started to process the request but the request is expected to be processed by a
                    // filter further down the chain.

        private Object reason;
        private int statusCode;

        VerilyFilterAction(){}
        VerilyFilterAction(Object reason){this.reason = reason;}

        public Object getReason(){return this.reason;}
        public void   setReason(Object reason){this.reason=reason;}
        public int getStatusCode(){return this.statusCode;}
        public void setStatusCode(int statusCode){this.statusCode=statusCode;}


    }


    public abstract VerilyFilterAction handleRequest(Request request, Response response, VerilyEnv env, VerilyFilterAction lastFilterResult);


    public static VerilyFilterAction getFilterResponse(VerilyFilterAction action, int HTTP_STATUS, Object reason){
        action.setStatusCode(HTTP_STATUS);
        action.setReason(reason);
        return action;
    }

    public static VerilyFilterAction getFilterResponse(VerilyFilterAction action, int HTTP_STATUS){
        return getFilterResponse(action, HTTP_STATUS,null);
    }



}
