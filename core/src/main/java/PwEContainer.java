/*
 * PwEContainer.java
 *
 * The main container for PwE. Handles all the dispatching and decoding for PwE.
 *
 */

import content.TemplateFactory;
import exceptions.InvalidFormalArgumentsException;
import freemarker.template.Template;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwe.lang.PwEMethod;
import pwe.lang.PwETable;
import pwe.lang.exceptions.MethodNotMappedException;
import pwe.lang.exceptions.TableHomomorphismException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Set;

public class PwEContainer implements Container {

    final Logger logger = LoggerFactory.getLogger(PwEContainer.class);

    private PwEEnv env;

    private PwEContainer(PwEEnv env) {
        this.setEnv(env);
        logger.info("Constructed new PwE container @ {}", new Date());
    }

    private static PwEContainer pWe;

    public static PwEContainer getContainer() throws IOException, TableHomomorphismException {
        if (pWe == null) {

            /**
             * Construct our world
             */
            PwEEnv env = new PwEEnv();

            // where are we?
            env.setHome(Paths.get(""));

            //verify that this is a PwE application
            //env.getHome().

            // set up the translation table
            Harness applicationHarness = new Harness(env.getHome());

            PwETable translationTable = applicationHarness.extractTranslationTable();

            env.setTranslationTable(translationTable);

            pWe = new PwEContainer(env);
        }
        return pWe;
    }


    public PwEMethod getMethodForRequest(Request r) throws MethodNotMappedException {

        String method = r.getPath().getName();
        String context = PwEUtil.trimRequestContext(r.getPath().getDirectory());

        // we don't need to check for null here, really.
        return env.getTranslationTable().methodAt(context, method);
    }


    public void marshallRequestToMethod(Request r, PwEMethod m) throws InvalidFormalArgumentsException {

        Set<String> paramKeys = r.getQuery().keySet();

        // Test 1: We should have as many parameters as the number of parameters of the method MINUS the number of
        if (m.getNonSessionBoundParameters().size() != paramKeys.size()) {
            throw new InvalidFormalArgumentsException("Invalid number of formal paramters");
        }
    }

    @Override
    public void handle(Request request, Response response) {

        try {

            // Step 1 - Map the incoming request onto a method call
            PwEMethod m = getMethodForRequest(request);

            // Step 2 - Decode the incoming request's parameters
            //
            // Note: We look at the request and try to match it with the method, not the other way around.
            //
            marshallRequestToMethod(request, m);

        } catch (MethodNotMappedException e) {
            send404(request, response);
        } catch (InvalidFormalArgumentsException e) {
            // show message about this.
        }

        // Step 3 - Identify session-bound parameters

        // Step 4 - Transform the mapped request and parameters into a method call, perform session value substitution

        // Step 5 - Perform the Method side of the invocation

        // Step 6 - Persist the writablly bound paramters to the session storage

        // Step 7 - Using the exact same parameters, invoke the controller method.

        // Step 8 - TBD: In the Maven POM for the PwE project (poll, for example) there should be a dependancy entry for
        //          a minimal set of templating libraries for dealing with the actual creation of the pages. Freemarker seems to be a likely canidate
        //          for this.
        try {

            Class c = Class.forName("methods.TestBasic", false, this.getClass().getClassLoader());
            c.getMethod("dispatchTest", null).invoke(null);


            PrintStream body = response.getPrintStream();
            long time = System.currentTimeMillis();

            response.setValue("Content-Type", "");
            response.setValue("Server", "");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);

            body.println("Hello World");
            body.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO - Implement these internal status responses as pretty templates
    public void send404(Request request, Response response) {

        try {

            Writer body = new OutputStreamWriter(response.getOutputStream());

            long time = System.currentTimeMillis();

            response.setValue("Content-Type", "");
            response.setValue("Server", "");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setCode(404);

            try {
                Template t = TemplateFactory.getInstance().get404FileTemplate();
                t.process(null, body);
            } catch (IOException e) {
                logger.error("Error during render of 404 template: {}", e.getMessage());
                body.write("Sorry, but the endpoint you requested does not exist.");
            } finally {
                body.close();
            }


        } catch (Exception e) { // this is horribly fatal.
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

    public PwEEnv getEnv() {
        return env;
    }

    public void setEnv(PwEEnv env) {
        this.env = env;
    }
}
