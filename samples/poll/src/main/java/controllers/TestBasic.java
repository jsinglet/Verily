package controllers;

public class TestBasic {

    // tests a simple function
    public static final void dispatchTest() {
        System.out.println("This method was dynamically dispatched.");
    }

    public static final void simpleFunction(String arg1) {
        System.out.println("Hey, you passed in the string: " + arg1);
    }


}