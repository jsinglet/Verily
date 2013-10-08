package verily.lang;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: jsinglet
 * Date: 7/1/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class VerilyTypeTest {

    @Test
    public void testEquals() throws Exception {

        PwEType t1 = new PwEType("String", "param1");
        PwEType t2 = new PwEType("String", "param1");
        PwEType t3 = new PwEType("String", "param2");

        assertTrue(t1.equals(t2));
        assertFalse(t1.equals(t3));

    }

    @Test
    public void testHashCode() throws Exception {

    }
}
