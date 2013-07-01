import java.util.Locale;
import java.util.ResourceBundle;

public class PwEUtil {

    private static final ResourceBundle msgResources;

    static {
        msgResources = ResourceBundle.getBundle("ApplicationMessages", Locale.getDefault());
    }

    public static String getMessage(String key) {
        return msgResources.getString(key);
    }

}
