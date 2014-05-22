package core;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VerilyFilter {

    protected final Logger logger = LoggerFactory.getLogger(VerilyFilter.class);
    protected String filterName;

    public VerilyFilter(String filterName){
        this.filterName = filterName;
    }
    public String getFilterName(){
        return filterName;
    }
    public abstract VerilyChainableAction handleRequest(Request request, Response response, VerilyEnv env, VerilyChainableAction lastFilterResult);


    public static VerilyChainableAction getFilterResponse(VerilyChainableAction action, int HTTP_STATUS, Object reason){
        action.setStatusCode(HTTP_STATUS);
        action.setReason(reason);
        return action;
    }

    public static VerilyChainableAction getFilterResponse(VerilyChainableAction action, int HTTP_STATUS){
        return getFilterResponse(action, HTTP_STATUS,null);
    }



}
