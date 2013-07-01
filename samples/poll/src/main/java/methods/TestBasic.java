package methods;

import java.util.List;

public class TestBasic {
    public static final void someFunction(String args) {
    }


    public static final String someFunction3(List<String> args) {

        someFunction("test");

        return "test";

    }


    public static final void someFunction2(List<String> args) {

        someFunction("test");

    }


    class TestInner1 {
        class TestInner2 {
            class TestInner3 {

            }
        }
    }

}