import java.io.IOException;
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

    public static void compileProject() throws IOException {
        Process p = new ProcessBuilder("mvn", "jar").start();
    }

}
