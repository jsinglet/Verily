package core.filters;

import core.ResponseUtils;
import core.VerilyContainer;
import core.VerilyEnv;
import core.VerilyFilter;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verily.lang.util.MRRTableSet;

import java.net.URL;

import static core.VerilyFilter.VerilyFilterAction.*;
import static core.Constants.*;
import static core.ResponseUtils.*;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class StaticContentFilter extends VerilyFilter {

    private static String filterName = "VerilyStaticContentFilter";

    public StaticContentFilter(){super(filterName);}


    @Override
    public VerilyFilterAction handleRequest(Request request, Response response, VerilyEnv env, VerilyFilterAction lastFilterResult) {

        URL u = this.getClass().getResource(request.getPath().getPath());

        if (u != null && u.getPath().endsWith("/") == false) { // static content
            sendFile(u, response, logger);

            return getFilterResponse(STOP, HTTP_OK);
        }
        return CONTINUE;
    }
}
