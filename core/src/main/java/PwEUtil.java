import exceptions.PwECompileFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.ResourceBundle;

public class PwEUtil {

    private static final ResourceBundle msgResources;

    static {
        msgResources = ResourceBundle.getBundle("content.ApplicationMessages", Locale.getDefault());
    }

    public static String getMessage(String key) {
        return msgResources.getString(key);
    }

    public static void compileProject() throws IOException, InterruptedException, PwECompileFailedException {

        Process p = new ProcessBuilder("mvn", "package").redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        int c = isr.read();

        while (c != -1) {
            System.out.write(c);
            c = isr.read();
        }

        is.close();

        int exitStatus = p.waitFor();


        if (exitStatus != 0) {
            throw new PwECompileFailedException("Building project failed. Please see output for details.");
        }
    }

    public static String trimRequestContext(String ctx) {
        if (ctx != null) {

            int idx1, idx2;

            // find the first slash
            for (idx1 = 0; idx1 < ctx.length() && ctx.charAt(idx1) == '/'; idx1++) ;

            // no slashes
            if (idx1 == ctx.length()) {
                return ctx;
            }

            // find the first slash
            for (idx2 = ctx.length() - 1; idx2 > idx1 && ctx.charAt(idx2) == '/'; idx2--) ;

            return ctx.substring(idx1, idx2 + 1);
        }

        return ctx;
    }

}
