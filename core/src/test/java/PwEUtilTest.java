import core.VerilyFilter;
import org.junit.Test;
import utils.VerilyUtil;

import static org.junit.Assert.assertTrue;

import static core.VerilyFilter.VerilyFilterAction.*;

public class PwEUtilTest {
    @Test
    public void testTrimRequestContext() throws Exception {

        String tests[] = new String[]{"SomeContext",
                "/SomeContext/",
                "//SomeContext/",
                "//SomeContext//",
                "SomeContext//",
                "/SomeContext",
                "////SomeContext////"};

        for (String t : tests) {
            System.out.println("Verifying: " + t);
            assertTrue(VerilyUtil.trimRequestContext(t).equals("SomeContext"));
        }

        String edge = "//Some/Context//";

        assertTrue(VerilyUtil.trimRequestContext(edge).equals("Some/Context"));

    }

    @Test
    public void testEnumPassing(){

        VerilyFilter.VerilyFilterAction a1 = OK;
        VerilyFilter.VerilyFilterAction a2 = ERROR;

        VerilyFilter.VerilyFilterAction b1 = OK;
        VerilyFilter.VerilyFilterAction b2 = ERROR;

        assertTrue(a1==b1);
        assertTrue(a2==b2);

        b1.setReason("test");
        b2.setReason("test");

        assertTrue(a1==b1);
        assertTrue(a2==b2);

        assertTrue(b1.getReason().equals("test"));
        assertTrue(b2.getReason().equals("test"));

        assertTrue(a1!=a2);
        assertTrue(b1!=b2);

        b1.setStatusCode(100);
        b2.setStatusCode(200);


        assertTrue(a1==b1);
        assertTrue(a2==b2);

        assertTrue(b1==OK);
        assertTrue(b2==ERROR);

        assertTrue(b1.getReason().equals("test"));
        assertTrue(b2.getReason().equals("test"));

        assertTrue(a1!=a2);
        assertTrue(b1!=b2);


    }
}
