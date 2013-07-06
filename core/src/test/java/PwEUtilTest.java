import org.junit.Test;

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
            assertTrue(PwEUtil.trimRequestContext(t).equals("SomeContext"));
        }

        String edge = "//Some/Context//";

        assertTrue(PwEUtil.trimRequestContext(edge).equals("Some/Context"));

    }
}
