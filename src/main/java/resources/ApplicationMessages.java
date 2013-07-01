package resources;

import java.util.ListResourceBundle;

public class ApplicationMessages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"MsgInvalidPort", "The specified port is in an invalid format. Please specify a numeric port format. Eg: 8000."},
                {"MsgParsingFailed", "Parsing Failed. Reason: "},
                {"MsgContainerInitFailed", "Failed to initialize PwE container. Reason: "},
                {"MsgTableHomomorphism", "There is an inconsistency in your Method to Controller mappings. In MeVC, each mapped method must have a corresponding mapped controller.\nThese functions much match in type, name, and arity."}
        };
    }
}
