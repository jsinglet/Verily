package core.filters;

import core.VerilyEnv;
import core.VerilyFilter;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;


import static core.VerilyFilter.VerilyFilterAction.*;
import static core.Constants.*;
import static core.ResponseUtils.*;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class ContractValidationFilter extends VerilyFilter {

    protected static String filterName = "VerilyContractValidationFilter";

    public ContractValidationFilter(){super(filterName);}


    @Override
    public VerilyFilterAction handleRequest(Request request, Response response, VerilyEnv env, VerilyFilterAction lastFilterResult) {
       if(lastFilterResult!=CONTINUE || !(lastFilterResult.getReason() instanceof AssertionError)){
           return CONTINUE;
       }

        return OK;
    }
}
