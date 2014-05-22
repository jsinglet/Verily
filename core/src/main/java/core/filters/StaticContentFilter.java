package core.filters;

import core.VerilyChainableAction;
import core.VerilyEnv;
import core.VerilyFilter;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.net.URL;

import static core.VerilyChainableAction.*;
import static core.Constants.*;
import static core.ResponseUtils.*;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class StaticContentFilter extends VerilyFilter {

    private static String filterName = "VerilyStaticContentFilter";

    public StaticContentFilter(){super(filterName);}


    @Override
    public VerilyChainableAction handleRequest(Request request, Response response, VerilyEnv env, VerilyChainableAction lastFilterResult) {

        URL u = this.getClass().getResource(request.getPath().getPath());

        if (u != null && u.getPath().endsWith("/") == false) { // static content
            sendFile(u, response, logger);

            return getFilterResponse(STOP, HTTP_OK);
        }
        return CONTINUE;
    }
}
