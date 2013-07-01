import exceptions.PwECompileFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.ResourceBundle;

public class PwEUtil {

    private static final ResourceBundle msgResources;

    static {
        msgResources = ResourceBundle.getBundle("resources.ApplicationMessages", Locale.getDefault());
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

}
