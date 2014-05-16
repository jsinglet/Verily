package core.filters;

import core.VerilyChainableAction;
import core.VerilyEnv;
import core.VerilyFilter;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;


import static core.VerilyChainableAction.*;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class ContractValidationFilter extends VerilyFilter {

    protected static String filterName = "VerilyContractValidationFilter";

    public ContractValidationFilter(){super(filterName);}


    @Override
    public VerilyChainableAction handleRequest(Request request, Response response, VerilyEnv env, VerilyChainableAction lastFilterResult) {
       if(lastFilterResult!=CONTINUE || !(lastFilterResult.getReason() instanceof AssertionError)){
           return CONTINUE;
       }

        return OK;
    }
}
