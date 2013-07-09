package methods;

public class TestBasic {
    public static final void dispatchTest() {
        System.out.println("Hey mother fuckers.");
    }

    public static final void simpleFunction(String arg1) {
        System.out.println("Hey, you passed in the string: " + arg1);
    }

    public static final void simpleFunction2(Integer arg1) {
        System.out.println("Hey, you passed in the Integer: " + arg1);
    }

    public static final void simpleFunction3(int arg1) {
        System.out.println("Hey, you passed in the raw Integer: " + arg1);
    }

    public static final void simpleFunction4(String arg1, Integer arg2, int arg3) {
        System.out.println(String.format("Hey, you passed in the String %s, The Object Integer %d, and the primative integer %d ", arg1, arg2, arg3));
    }

}