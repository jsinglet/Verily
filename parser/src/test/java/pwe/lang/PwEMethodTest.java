package pwe.lang;

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
public class PwEMethodTest {
    @Test
    public void testEquals() throws Exception {

        //verify that two simple methods will be declared equal
        List<PwEType> fp1 = Arrays.asList(
                new PwEType("String", "param1"),
                new PwEType("String", "param2"),
                new PwEType("String", "param3")
        );

        List<PwEType> fp2 = Arrays.asList(
                new PwEType("String", "param1"),
                new PwEType("String", "param2"),
                new PwEType("String", "param3")
        );

        List<PwEType> fp3 = Arrays.asList(
                new PwEType("String", "param"),
                new PwEType("String", "param2"),
                new PwEType("String", "param3")
        );

        List<PwEType> fp4 = Arrays.asList(
                new PwEType("String", "param1"),
                new PwEType("String", "param2")
        );

        List<PwEType> fp5 = Arrays.asList(
                new PwEType("void", "param"),
                new PwEType("String", "param2"),
                new PwEType("String", "param3")
        );


        PwEMethod m1 = new PwEMethod("myMethod", fp1);
        PwEMethod m2 = new PwEMethod("myMethod", fp2);
        PwEMethod m3 = new PwEMethod("myMethod", fp3);
        PwEMethod m4 = new PwEMethod("myMethod", fp4);
        PwEMethod m5 = new PwEMethod("myMethod", fp5);
        PwEMethod m6 = new PwEMethod("myMethod1", fp1);

        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(m3));
        assertFalse(m1.equals(m4));
        assertFalse(m1.equals(m5));
        assertFalse(m1.equals(m6));


    }
}
