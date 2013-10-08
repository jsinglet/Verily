import org.junit.Test;
import utils.VerilyUtil;

import static org.junit.Assert.assertTrue;


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
}
