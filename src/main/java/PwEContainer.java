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

import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Date;

public class PwEContainer implements Container {

    final Logger logger = LoggerFactory.getLogger(PwEContainer.class);

    private PwEEnv env;

    private PwEContainer(PwEEnv env) {
        this.env = env;
        logger.info("Constructed new PwE container @ {}", new Date());
    }

    private static PwEContainer pWe;

    public static PwEContainer getContainer() {
        if (pWe == null) {

            /**
             * Construct our world
             */
            PwEEnv env = new PwEEnv();

            //where are we?
            env.setHome(Paths.get(""));

            //verify that this is a PwE application
            //env.getHome().

            pWe = new PwEContainer(env);
        }
        return pWe;
    }

    @Override
    public void handle(Request request, Response response) {

        try {
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
}
