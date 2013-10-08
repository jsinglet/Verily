package content;

import java.util.ListResourceBundle;

public class ApplicationMessages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"MsgInvalidPort", "The specified port is in an invalid format. Please specify a numeric port format. Eg: 8000."},
                {"MsgParsingFailed", "Parsing Failed. Reason: "},
                {"MsgInitFailed", "Project initialization failed. Reason: "},
                {"MsgContainerInitFailed", "Failed to initialize Verily container. Reason: "},
                {"MsgTableHomomorphism", "There is an inconsistency in your Method to Router mappings. In MRR, each mapped method must have a corresponding mapped Router.\nThese functions much match in type, name, and arity."},
                {"MsgInvalidDirectoryFormat", "Unable to locate your Routers and Methods in src/main/java/{routers,methods}. If you haven't already, run:\n\n\t$ verily init\n\nThis will initialize this directory as a Verily project and create the needed directories."},
                {"MsgCompileFailed", "Maven compilation of project failed. Please check output for more details."}

        };
    }
}
