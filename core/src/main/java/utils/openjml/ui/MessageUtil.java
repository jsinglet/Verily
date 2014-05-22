package utils.openjml.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import utils.openjml.ui.res.ApplicationMessages;

public class MessageUtil {
    
    
    
    private static final ResourceBundle msgResources;


    static {
        msgResources = ResourceBundle.getBundle("utils.openjml.ui.res.ApplicationMessages", Locale.getDefault());
    }

    public static String getMessage(ApplicationMessages.ApplicationMessageKey key) {
        return msgResources.getString(key.toString());
    }


}
