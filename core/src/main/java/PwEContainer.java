/*
 * PwEContainer.java
 *
 * The main container for PwE. Handles all the dispatching and decoding for PwE.
 *
 */

import content.TemplateFactory;
import exceptions.InvalidFormalArgumentsException;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwe.lang.*;
import pwe.lang.exceptions.MethodNotMappedException;
import pwe.lang.exceptions.TableHomomorphismException;
import reification.*;
import utils.PwEUtil;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class PwEContainer implements Container {

    private static PwEContainer pWe;
    private Thread classReloader;
    private PwEModificationWatcher modificationWatcher;

    final Logger logger = LoggerFactory.getLogger(PwEContainer.class);
    private PwEEnv env;

    private PwEContainer(PwEEnv env) {
        this.setEnv(env);
        logger.info("Constructed new PwE container @ {}", new Date());
    }

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

    public String classContextFromRequest(Request r) {
        return PwEUtil.trimRequestContext(r.getPath().getDirectory());
    }

    public PwEMethod getMethodForRequest(Request r) throws MethodNotMappedException {

        String method = r.getPath().getName();
        String context = classContextFromRequest(r);

        // we don't need to check for null here, really.
        return env.getTranslationTable().methodAt(context, method);
    }

    public List<Object> marshallRequestToMethod(Request r, PwEMethod m, Context ctx, boolean readOnly) throws InvalidFormalArgumentsException {

        // get the sesssion
        Session session = MemoryBackedSession.withContext(ctx);

        Set<String> paramKeys = r.getQuery().keySet();

        // Test 1: We should have as many parameters as the number of parameters of the method MINUS the number of
        if (m.getNonSessionBoundParameters().size() != paramKeys.size()) {
            throw new InvalidFormalArgumentsException("Invalid number of formal parameters");
        }

        // Test 2: Create an ordered list of parameters and see if we can fill it in.
        List<Object> actualParameters = new ArrayList<Object>(m.getFormalParameters().size());

        for (PwEType requestedType : m.getFormalParameters()) {

            // look it up in the session table
            if (requestedType.isSessionBound()) {

                logger.info("Decoding bound formal parameter {} with type {}", requestedType.getName(), requestedType.getType());

                WritableValue v = session.getValue(requestedType.getName());

                // if this is null, we have to initialize it

                if (v == null) {

                    // this type is always a base writable type
                    v = new WritableValue(null);

                    session.setValue(requestedType.getName(), v);
                }

                // see if we need to make a read only copy of this variable
                if (readOnly) {

                    if (v.getValue() != null) {
                        ReadableValue rov = new ReadableValue(v.getValue());//(Serializable) SerializationUtils.clone(v.getValue()));
                        actualParameters.add(rov);

                    }else{
                        actualParameters.add(new ReadableValue(null));
                    }


                } else {
                    actualParameters.add(v);
                }

            } else { // deduce from the request

                logger.info("Decoding unbound formal parameter {} with type {}", requestedType.getName(), requestedType.getType());

                if (paramKeys.contains(requestedType.getName())) {

                    // coerce it into the requested type
                    // eg: String, Integer, etc.
                    // we know about some of these types internally and can convert them (see below)
                    // however, if it is a custom type that type must have a noargs constructor.
                    actualParameters.add(PwEUtil.coerceToType(requestedType, r.getQuery().get(requestedType.getName())));

                } else {
                    throw new InvalidFormalArgumentsException(String.format("Required parameter \"%s\" not found in the incoming request.", requestedType.getName()));
                }
            }
        }

        return actualParameters;
    }

    public void persistSessionInformation(PwEMethod m, Object[] params, Context ctx) {

        Session session = MemoryBackedSession.withContext(ctx);

        for (int i = 0; i < m.getFormalParameters().size(); i++) {

            if (m.getFormalParameters().get(i).isSessionWritable()) {
                session.updateValue(m.getFormalParameters().get(i).getName(), (WritableValue) params[i]);
            }

        }


    }

    public Context getOrEstablishSession(Request request, Response response) {

        // use a current session
        if (request.getCookie(PwE.SESSION_COOKIE) != null) {

            Context ctx = new Context(request.getCookie(PwE.SESSION_COOKIE).getValue());

            logger.info("Reconnected old session {}", ctx.toString());
            return ctx;
        }

        Context ctx = new Context();
        // establish a new one
        Cookie c = new Cookie(PwE.SESSION_COOKIE, ctx.toString());

        response.setCookie(c);

        logger.info("Established new session {}", ctx.toString());

        return ctx;
    }

    @Override
    public void handle(Request request, Response response) {

        Context ctx = getOrEstablishSession(request, response);
        String classContext = classContextFromRequest(request);


        // these are filled in later
        PwEMethod m = null;

        int statusCode = 200;
        long ts1 = System.currentTimeMillis();


        // Check if it is a file (overrides)
        URL u = this.getClass().getResource(request.getPath().getPath());

        if (u != null && u.getPath().endsWith("/") == false) { // static content
            sendFile(u, response);
            statusCode = 200;
        } else {         // dynamic content

            try {

                // Step 1 - Map the incoming request onto a method call

                m = getMethodForRequest(request);

                // Step 2 - Decode the incoming request's parameters
                //
                // Note: We look at the request and try to match it with the method, not the other way around.
                // This process:
                //      - Will identify session-bound parameters
                //      - Transform the mapped request and parameters into a method call, perform session value substitution

                List<Object> actualParameters = marshallRequestToMethod(request, m, ctx, false);

                // Step 3 - Perform the Method side of the invocation

                Class c = Class.forName(String.format("methods.%s", classContext), false, new PwEClassLoader(this.getClass().getClassLoader()));
                Object args[] = actualParameters.toArray(new Object[actualParameters.size()]);
                Class clazz[] = new Class[args.length];

                for (int i = 0; i < args.length; i++) {
                    // in this particular case we might have to translate to primatives
                    if(m.getFormalParameters().get(i).isSessionWritable()){
                        clazz[i] = WritableValue.class;
                    }else if(m.getFormalParameters().get(i).isSessionReadable()){
                        clazz[i] = ReadableValue.class;
                    }else{
                        clazz[i] = PwEUtil.translatedType(m.getFormalParameters().get(i), args[i].getClass());
                    }
                }

                c.getMethod(m.getMethod(), clazz).invoke(null, args);


                long ts2 = System.currentTimeMillis();

                // log the request.
                logger.info("[{}] - Finished Executing Method \"{}.{}\" ({} ms)", new Date(), classContext, m.getMethod(), ts2 - ts1);


                // Step 4 - Persist the writablly bound parameters to the session storage

                // this happens automatically since we are using memory backed sessions
                // however, we implement this here as an no-op so that adding other session
                // providers is straightforward (read: clear) in the future

                persistSessionInformation(m, args, ctx);

                //
                // To ensure that no tainting happened during the method call, we recalculate the actual parameters -- making sure to copy any session variables.
                //

                List<Object> controllerActualParameters = marshallRequestToMethod(request, m, ctx, true);
                Object controllerArgs[] = controllerActualParameters.toArray(new Object[controllerActualParameters.size()]);
                Class controllerClazz[] = new Class[controllerArgs.length];

                for (int i = 0; i < controllerArgs.length; i++) {
                    // in this particular case we might have to translate to primatives
                    controllerClazz[i] = PwEUtil.translatedType(m.getFormalParameters().get(i), controllerArgs[i].getClass());
                }


                // Step 7 - Using the exact same parameters, invoke the controller method.
                Class controller = Class.forName(String.format("controllers.%s", classContext), false, new PwEClassLoader(this.getClass().getClassLoader()));


                Content content = (Content) controller.getMethod(m.getMethod(), controllerClazz).invoke(null, controllerArgs);


                long ts3 = System.currentTimeMillis();

                // log the request.
                logger.info("[{}] - Finished Executing Controller \"{}.{}\" ({} ms)", new Date(), classContext, m.getMethod(), ts3 - ts2);


                OutputStream out = response.getOutputStream();

                long time = System.currentTimeMillis();

                response.setValue("Content-Type", content.getContentType());
                response.setValue("Server", "PwE-Powered");
                response.setDate("Date", time);
                response.setDate("Last-Modified", time);
                response.setCode(content.getContentCode());

                out.write(content.getContent().getBytes());

                out.close();

            } catch (MethodNotMappedException e) {
                statusCode = 404;
                send404(request, response);
            } catch (InvalidFormalArgumentsException e) {
                // show message about this.
                statusCode = 400;
                send400(request, response, classContext, m, e.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
                statusCode = 500;
                send500(request, response, classContext, m, e.getMessage());
            }

        }
        long ts2 = System.currentTimeMillis();

        // log the request.
        logger.info("[{}] - \"{} {}\" {} ({} ms)", new Date(), request.getMethod(), request.getPath().getPath(), statusCode, ts2 - ts1);

    }

    public void sendFile(URL file, Response response) {
        try {

            long time = System.currentTimeMillis();

            OutputStream out = response.getOutputStream();
            InputStream in = file.openStream();


            response.setValue("Content-Type", PwEUtil.mimeForType(file));
            response.setValue("Server", "PwE");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setCode(200);


            try {
                IOUtils.copy(file.openStream(), response.getOutputStream());
            } catch (IOException e) {
                logger.error("Error during render of static file: {}", e.getMessage());
            } finally {
                out.close();
                in.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void send400(Request request, Response response, String context, PwEMethod target, String specificError) {

        try {

            Writer body = new OutputStreamWriter(response.getOutputStream());

            long time = System.currentTimeMillis();

            response.setValue("Content-Type", "text/html");
            response.setValue("Server", "");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setCode(404);

            try {

                Map<String, String> vars = new HashMap<String, String>();

                vars.put("version", PwE.VERSION);
                vars.put("targetClass", context);
                vars.put("targetMethod", target.getMethod());
                vars.put("message", specificError);

                Template t = TemplateFactory.getInstance().get400Template();
                t.process(vars, body);
            } catch (IOException e) {
                logger.error("Error during render of 400 template: {}", e.getMessage());
                body.write("Sorry, but the endpoint you requested does not exist.");
            } finally {
                body.close();
            }


        } catch (Exception e) { // this is horribly fatal.
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

    public void send500(Request request, Response response, String context, PwEMethod target, String specificError) {

        try {

            Writer body = new OutputStreamWriter(response.getOutputStream());

            long time = System.currentTimeMillis();

            response.setValue("Content-Type", "text/html");
            response.setValue("Server", "");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setCode(500);

            try {

                Map<String, String> vars = new HashMap<String, String>();

                String message = "No details available. Please check the application logs";

                if(specificError!=null){
                    message = specificError;
                }

                vars.put("version", PwE.VERSION);
                vars.put("targetClass", context);
                vars.put("targetMethod", target.getMethod());
                vars.put("message", message);

                Template t = TemplateFactory.getInstance().get500Template();
                t.process(vars, body);
            } catch (IOException e) {
                logger.error("Error during render of 500 template: {}", e.getMessage());
                body.write("Sorry, but the endpoint you requested does not exist.");
            } finally {
                body.close();
            }


        } catch (Exception e) { // this is horribly fatal.
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

    public void send404(Request request, Response response) {

        try {

            Writer body = new OutputStreamWriter(response.getOutputStream());

            long time = System.currentTimeMillis();

            response.setValue("Content-Type", "text/html");
            response.setValue("Server", "");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            response.setCode(404);

            try {

                Map<String, String> vars = new HashMap<String, String>();

                vars.put("version", PwE.VERSION);

                Template t = TemplateFactory.getInstance().get404Template();

                t.process(vars, body);
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


    public void stopService(){
        if(classReloader!=null){
            modificationWatcher.scheduleShutdown();
        }
    }
    public void startServices(){
        if(getEnv().isReload()){
            logger.info("Starting class reloading service...");

            modificationWatcher = new PwEModificationWatcher(Paths.get("").resolve("src").resolve("main"));

            classReloader = new Thread(modificationWatcher);

            classReloader.start();


            logger.info("Done");
        }
    }


}
