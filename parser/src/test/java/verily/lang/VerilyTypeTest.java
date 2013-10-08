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

        VerilyType t1 = new VerilyType("String", "param1");
        VerilyType t2 = new VerilyType("String", "param1");
        VerilyType t3 = new VerilyType("String", "param2");

        assertTrue(t1.equals(t2));
        assertFalse(t1.equals(t3));

    }

    @Test
    public void testHashCode() throws Exception {

    }
}
