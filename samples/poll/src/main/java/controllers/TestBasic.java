package controllers;

import pwe.lang.Content;
import pwe.lang.TextContent;
import pwe.lang.WritableValue;

public class TestBasic {

    public static final Content simpleFunction(String arg1) {
        System.out.println("Executing controller with argument " + arg1);

        return new TextContent("I am real content from the controller!");

    }
}