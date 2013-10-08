package verily.lang;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: jsinglet
 * Date: 7/1/13
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class VerilyMethodTest {
    @Test
    public void testEquals() throws Exception {

        //verify that two simple methods will be declared equal
        List<VerilyType> fp1 = Arrays.asList(
                new VerilyType("String", "param1"),
                new VerilyType("String", "param2"),
                new VerilyType("String", "param3")
        );

        List<VerilyType> fp2 = Arrays.asList(
                new VerilyType("String", "param1"),
                new VerilyType("String", "param2"),
                new VerilyType("String", "param3")
        );

        List<VerilyType> fp3 = Arrays.asList(
                new VerilyType("String", "param"),
                new VerilyType("String", "param2"),
                new VerilyType("String", "param3")
        );

        List<VerilyType> fp4 = Arrays.asList(
                new VerilyType("String", "param1"),
                new VerilyType("String", "param2")
        );

        List<VerilyType> fp5 = Arrays.asList(
                new VerilyType("void", "param"),
                new VerilyType("String", "param2"),
                new VerilyType("String", "param3")
        );


        VerilyMethod m1 = new VerilyMethod("myMethod", fp1);
        VerilyMethod m2 = new VerilyMethod("myMethod", fp2);
        VerilyMethod m3 = new VerilyMethod("myMethod", fp3);
        VerilyMethod m4 = new VerilyMethod("myMethod", fp4);
        VerilyMethod m5 = new VerilyMethod("myMethod", fp5);
        VerilyMethod m6 = new VerilyMethod("myMethod1", fp1);

        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(m3));
        assertFalse(m1.equals(m4));
        assertFalse(m1.equals(m5));
        assertFalse(m1.equals(m6));


    }
}
