package verily.lang;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class VerilyTableTest {

    PwETable t;
    PwEMethod m;
    List<PwEType> formalParameters;


    @Before
    public void setUp() throws Exception {
        t = new PwETable();

        formalParameters = Arrays.asList(
                new PwEType("String", "param1"),
                new PwEType("String", "param2"),
                new PwEType("String", "param3")
        );

        m = new PwEMethod("myMethod", formalParameters);
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
