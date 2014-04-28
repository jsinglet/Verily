package core;

import content.TemplateFactory;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.VerilyUtil;
import verily.lang.VerilyMethod;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static core.Constants.*;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class ResponseUtils {

    public static void sendFile(URL file, Response response, Logger logger) {
        try {

            long time = System.currentTimeMillis();

            OutputStream out = response.getOutputStream();
            InputStream in = file.openStream();


            response.setValue(CONTENT_TYPE, VerilyUtil.mimeForType(file));
            response.setValue(SERVER, "Verily-Powered");
            response.setDate(DATE, time);
            response.setDate(LAST_MODIFIED, time);
            response.setCode(HTTP_OK);


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

    public static void send400(Request request, Response response, String context, VerilyMethod target, String specificError, Logger logger) {

        try {

            Writer body = new OutputStreamWriter(response.getOutputStream());

            long time = System.currentTimeMillis();

            response.setValue(CONTENT_TYPE, "text/html");
            response.setValue(SERVER, SERVER_NAME);
            response.setDate(DATE, time);
            response.setDate(LAST_MODIFIED, time);
            response.setCode(HTTP_400);

            try {

                Map<String, String> vars = new HashMap<String, String>();

                vars.put("version", Verily.VERSION);
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

    public static void send404(Request request, Response response, Logger logger) {

        try {

            Writer body = new OutputStreamWriter(response.getOutputStream());

            long time = System.currentTimeMillis();

            response.setValue(CONTENT_TYPE, "text/html");
            response.setValue(SERVER, SERVER_NAME);
            response.setDate(DATE, time);
            response.setDate(LAST_MODIFIED, time);
            response.setCode(HTTP_404);

            try {

                Map<String, String> vars = new HashMap<String, String>();

                vars.put("version", Verily.VERSION);

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

    public static void send500(Request request, Response response, String context, VerilyMethod target, String specificError, Logger logger) {

        try {

            Writer body = new OutputStreamWriter(response.getOutputStream());

            long time = System.currentTimeMillis();

            response.setValue(CONTENT_TYPE, "text/html");
            response.setValue(SERVER, SERVER_NAME);
            response.setDate(DATE, time);
            response.setDate(LAST_MODIFIED, time);
            response.setCode(HTTP_500);

            try {

                Map<String, String> vars = new HashMap<String, String>();

                String message = "No details available. Please check the application logs";

                if (specificError != null) {
                    message = specificError;
                }

                vars.put("version", Verily.VERSION);
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
}
