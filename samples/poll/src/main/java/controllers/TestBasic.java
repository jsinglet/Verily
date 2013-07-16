package controllers;

import pwe.lang.Content;
import pwe.lang.TemplateHTMLContent;
import pwe.lang.TextContent;
import pwe.lang.WritableValue;

import java.util.HashMap;
import java.util.Map;

public class TestBasic {

    public static final Content simpleFunction(String arg1) {
        System.out.println("Executing controller with argument " + arg1);

        return new TextContent("I am real content from the controller!");

    }

    public static final Content simpleFunction2(String msg) {

        Map<String,String> bindings = new HashMap<String,String>();

        bindings.put("title", "My Page");
        bindings.put("message", msg);

        return new TemplateHTMLContent("test1.ftl", bindings);

    }

}