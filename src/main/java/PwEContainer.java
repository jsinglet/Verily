/*
 * PwEContainer.java
 *
 * The main container for PwE. Handles all the dispatching and decoding for PwE.
 *
 */

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwe.lang.PwETable;
import pwe.lang.exceptions.TableHomomorphismException;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Date;

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

    @Override
    public void handle(Request request, Response response) {

        // Step 1 - Map the incoming request into it's context and method pairs

        // Step 2 - Decode the incoming request's parameters

        // Step 3 - Find a suitable method for dispatching

        // Step 4 - Identify session-bound parameters

        // Step 5 - Transform the mapped request and parameters into a method call, perform session value substitution

        // Step 6 - Perform the Method side of the invocation

        // Step 7 - Persist the writablly bound paramters to the session storage

        // Step 8 - Using the exact same parameters, invoke the controller method.

        // Step 9 - TBD: In the Maven POM for the PwE project (poll, for example) there should be a dependancy entry for
        //          a minimal set of templating libraries for dealing with the actual creation of the pages. Freemarker seems to be a likely canidate
        //          for this.
        try {

            Class c = Class.forName("methods.TestBasic", false, this.getClass().getClassLoader());
            c.getMethod("dispatchTest", null).invoke(null);


            PrintStream body = response.getPrintStream();
            long time = System.currentTimeMillis();

            response.setValue("Content-Type", "text/plain");
            response.setValue("Server", "HelloWorld/1.0 (PwE 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);

            body.println("Hello World");
            body.close();
        } catch (Exception e) {
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
