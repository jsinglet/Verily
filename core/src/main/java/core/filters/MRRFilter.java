package core.filters;

import core.*;
import exceptions.InvalidFormalArgumentsException;
import org.apache.commons.lang.SerializationUtils;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import reification.Context;
import reification.MemoryBackedSession;
import reification.Session;
import reification.VerilyClassLoader;
import utils.VerilyUtil;
import verily.lang.*;
import verily.lang.exceptions.MethodNotMappedException;

import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static core.VerilyChainableAction.*;
import static core.Constants.*;


/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class MRRFilter extends VerilyFilter {

    protected static String filterName = "VerilyMRRFilter";

    public MRRFilter(){super(filterName);}

    private VerilyEnv env;

    @Override
    public VerilyChainableAction handleRequest(Request request, Response response, VerilyEnv env, VerilyChainableAction lastFilterResult) {

        this.env = env;

        Context ctx = getOrEstablishSession(request, response);
        String classContext = classContextFromRequest(request);


        // these are filled in later
        VerilyMethod m = null;
        VerilyMethod r = null;

        long ts1 = System.currentTimeMillis();

        try {

            // Step 1 - Map the incoming request onto a method call

            m = getMethodForRequest(request);
            r = getRouterForRequest(request);

            // Step 2 - Decode the incoming request's parameters
            //
            // Note: We look at the request and try to match it with the method, not the other way around.
            // This process:
            //      - Will identify session-bound parameters
            //      - Transform the mapped request and parameters into a method call, perform session value substitution

            List<Object> actualParameters = marshallRequestToMethod(request, m, ctx, false);

            // Step 3 - Perform the Method side of the invocation

            Class c;

            if(VerilyContainer.getContainer().getEnv().isEnableContracts())
                c = Class.forName(String.format("methods.%sProxy", classContext), false, new VerilyClassLoader(this.getClass().getClassLoader()));
            else
                c = Class.forName(String.format("methods.%s", classContext), false, new VerilyClassLoader(this.getClass().getClassLoader()));

            Object args[] = actualParameters.toArray(new Object[actualParameters.size()]);
            Class clazz[] = new Class[args.length];

            for (int i = 0; i < args.length; i++) {
                // in this particular case we might have to translate to primatives
                if (m.getFormalParameters().get(i).isSessionWritable()) {
                    clazz[i] = WritableValue.class;
                } else if (m.getFormalParameters().get(i).isSessionReadable()) {
                    clazz[i] = ReadableValue.class;
                } else {
                    clazz[i] = VerilyUtil.translatedType(m.getFormalParameters().get(i), args[i].getClass());
                }
            }

            Object methodReturnValue = null;
            boolean fallback = false;

            try {
                methodReturnValue = c.getMethod(m.getMethod(), clazz).invoke(null, args);
            }catch(InvocationTargetException | RuntimeException e){
                if((e instanceof InvocationTargetException || e.getMessage().equals("_jmlValidationFail")) && VerilyContainer.getContainer().getEnv().isEnableContracts()){
                    fallback = true;
                    e.printStackTrace();
                }else{
                    throw new Exception(e);
                }

            }


            long ts2 = System.currentTimeMillis();

            // log the request.
            logger.info("[{}] - Finished Executing Method \"{}.{}\" ({} ms)", new Date(), classContext, m.getMethod(), ts2 - ts1);


            // Step 4 - Persist the writablly bound parameters to the session storage

            // this happens automatically since we are using memory backed sessions
            // however, we implement this here as an no-op so that adding other session
            // providers is straightforward (read: clear) in the future

            if(fallback==false)
                persistSessionInformation(m, args, ctx);

            //
            // To ensure that no tainting happened during the method call, we recalculate the actual parameters -- making sure to copy any session variables.
            //

            //
            // To make this easy, we marshall the actual parameters from the request for the METHOD, since the
            // final parameter (if it exists) won't be in the request (it's the return value of the method)
            //
            List<Object> controllerActualParameters = marshallRequestToMethod(request, m, ctx, true);


            //
            // As per the design, if the return type of the method isn't void (ie, null) then we
            // should pass in whatever the return type from the method is.
            //
            if (m.getType() != null && fallback==false) {
                controllerActualParameters.add(methodReturnValue);
            }

            Object controllerArgs[] = controllerActualParameters.toArray(new Object[controllerActualParameters.size()]);
            Class controllerClazz[] = new Class[controllerArgs.length];

            for (int i = 0; i < controllerArgs.length; i++) {
                controllerClazz[i] = VerilyUtil.translatedType(r.getFormalParameters().get(i), controllerArgs[i].getClass());
            }

            // Step 7 - Using the exact same parameters, invoke the router method.
            Class router;
            if(VerilyContainer.getContainer().getEnv().isEnableContracts() && fallback)
                router = Class.forName(String.format("methods.%sValidation", classContext), false, new VerilyClassLoader(this.getClass().getClassLoader()));
            else
                router = Class.forName(String.format("routers.%s", classContext), false, new VerilyClassLoader(this.getClass().getClassLoader()));

            Content content;

            if(VerilyContainer.getContainer().getEnv().isEnableContracts() && fallback)
                content = (Content) router.getMethod(m.getMethod(), clazz).invoke(null, args);
            else
                content = (Content) router.getMethod(m.getMethod(), controllerClazz).invoke(null, controllerArgs);



            long ts3 = System.currentTimeMillis();

            // log the request.
            logger.info("[{}] - Finished Executing Router \"{}.{}\" ({} ms)", new Date(), classContext, m.getMethod(), ts3 - ts2);


            OutputStream out = response.getOutputStream();

            long time = System.currentTimeMillis();

            response.setValue(CONTENT_TYPE, content.getContentType());
            response.setValue(SERVER, SERVER_NAME);
            response.setDate(DATE, time);
            response.setDate(LAST_MODIFIED, time);
            response.setCode(content.getContentCode());

            out.write(content.getContent().getBytes());

            out.close();

        } catch (MethodNotMappedException e) {
            return getFilterResponse(ERROR, HTTP_404);
        } catch (InvalidFormalArgumentsException e) {
            return getFilterResponse(ERROR, HTTP_400, new FilterError(classContext, e.getMessage(), m));
        } catch(AssertionError e){
          // TODO - pass forward information about which assertion failed.
            return CONTINUE;
        } catch (Exception e) {
            e.printStackTrace(); // TODO - remove this when we feel like unspecified errors are very rare.
            return getFilterResponse(ERROR, HTTP_500, new FilterError(classContext, e.getMessage(), m));
        }


        return OK;
    }

    //
    // Session handling
    //


    public Context getOrEstablishSession(Request request, Response response) {

        // use a current session
        if (request.getCookie(Verily.SESSION_COOKIE) != null) {

            Context ctx = new Context(request.getCookie(Verily.SESSION_COOKIE).getValue());

            logger.info("Reconnected old session {}", ctx.toString());
            return ctx;
        }

        Context ctx = new Context();
        // establish a new one
        Cookie c = new Cookie(Verily.SESSION_COOKIE, ctx.toString());

        response.setCookie(c);

        logger.info("Established new session {}", ctx.toString());

        return ctx;
    }


    public void persistSessionInformation(VerilyMethod m, Object[] params, Context ctx) {

        Session session = MemoryBackedSession.withContext(ctx);

        for (int i = 0; i < m.getFormalParameters().size(); i++) {

            if (m.getFormalParameters().get(i).isSessionWritable()) {
                session.updateValue(m.getFormalParameters().get(i).getName(), (WritableValue) params[i]);
            }

        }


    }

    public String classContextFromRequest(Request r) {
        return VerilyUtil.trimRequestContext(r.getPath().getDirectory());
    }



    public VerilyMethod getMethodForRequest(Request r) throws MethodNotMappedException {

        String method = r.getPath().getName();
        String context = classContextFromRequest(r);

        // we don't need to check for null here, really.
        return env.findMappedMethod(context, method);
    }

    public VerilyMethod getRouterForRequest(Request r) throws MethodNotMappedException {

        String method = r.getPath().getName();
        String context = classContextFromRequest(r);

        // we don't need to check for null here, really.
        return env.findMappedRouter(context, method);
    }


    public List<Object> marshallRequestToMethod(Request r, VerilyMethod m, Context ctx, boolean readOnly) throws InvalidFormalArgumentsException {

        // get the sesssion
        Session session = MemoryBackedSession.withContext(ctx);

        Set<String> paramKeys = r.getQuery().keySet();

        // Test 1: We should have as many parameters as the number of parameters of the method MINUS the number of
        if (m.getNonSessionBoundParameters().size() != paramKeys.size()) {
            throw new InvalidFormalArgumentsException("Invalid number of formal parameters");
        }

        // Test 2: Create an ordered list of parameters and see if we can fill it in.
        List<Object> actualParameters = new ArrayList<Object>(m.getFormalParameters().size());

        for (VerilyType requestedType : m.getFormalParameters()) {

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

                        Serializable clonedValue = (Serializable) SerializationUtils.clone(v.getValue());
                        ReadableValue rov = new ReadableValue(clonedValue);

                        //ReadableValue rov = new ReadableValue(v.getValue());//(Serializable) SerializationUtils.clone(v.getValue()));
                        actualParameters.add(rov);

                    } else {
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
                    actualParameters.add(VerilyUtil.coerceToType(requestedType, r.getQuery().get(requestedType.getName())));

                } else {
                    throw new InvalidFormalArgumentsException(String.format("Required parameter \"%s\" not found in the incoming request.", requestedType.getName()));
                }
            }
        }

        return actualParameters;
    }



}
