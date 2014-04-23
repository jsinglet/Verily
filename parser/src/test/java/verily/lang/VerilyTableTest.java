package verily.lang;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class VerilyTableTest {

    VerilyTable t;
    VerilyMethod m;
    List<VerilyType> formalParameters;


    @Before
    public void setUp() throws Exception {
        t = new VerilyTable();

        formalParameters = Arrays.asList(
                new VerilyType("String", "param1"),
                new VerilyType("String", "param2"),
                new VerilyType("String", "param3")
        );

        m = new VerilyMethod("myMethod", formalParameters, null,0);
    }

    @After
    public void tearDown() throws Exception {
        // no op
    }

    @Test
    public void testMapMethod() throws Exception {

        // map a method, make sure it is there.
        t.mapMethod("myContext", m);

        assertTrue(t.methodAt("myContext", "myMethod").equals(m));

    }

    @Test
    public void testMethodAt() throws Exception {

    }

    @Test
    public void testSize() throws Exception {

    }

    @Test
    public void testEquals() throws Exception {

    }
}
