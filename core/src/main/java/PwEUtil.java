import exceptions.InvalidFormalArgumentsException;
import exceptions.PwECompileFailedException;
import pwe.lang.PwEType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

    public static String mimeForType(URL file) {
        return URLConnection.guessContentTypeFromName(file.getFile());
    }


    public static Object coerceToType(PwEType type, String guess) throws InvalidFormalArgumentsException {

        if (guess == null) {
            return guess;
        }

        String s = guess.trim();

        try {
            if (type.getType().equals("Integer") || type.getType().equals("int")) {
                return Integer.parseInt(s);
            } else if (type.getType().equals("Boolean") || type.getType().equals("boolean")) {

                if (s.equalsIgnoreCase("true") || s.equals("1"))
                    return new Boolean(true);
                else if (s.equalsIgnoreCase("false") || s.equals("0"))
                    return new Boolean(false);
                else
                    throw new InvalidFormalArgumentsException(String.format("Cannot convert actual parameter value \"%s\" to a Boolean.", s));
            } else if (type.getType().equals("String")) {
                return s;
            } else {
                // TODO: Implement other data types
                throw new InvalidFormalArgumentsException(String.format("Cannot construct requested type \"%s\" because it is not yet implemented.", type.getType()));
            }

        } catch (Exception e) {
            throw new InvalidFormalArgumentsException(String.format("Cannot convert actual parameter value \"%s\" to a Boolean.", s));
        }
    }

}
