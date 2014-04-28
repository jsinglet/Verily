package core;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.VerilyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class ResponseUtils {

    public static void sendFile(URL file, Response response, Logger logger) {
        try {

            long time = System.currentTimeMillis();

            OutputStream out = response.getOutputStream();
            InputStream in = file.openStream();


            response.setValue("Content-Type", VerilyUtil.mimeForType(file));
            response.setValue("Server", "Verily-Powered");
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
}
