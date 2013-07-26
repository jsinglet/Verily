package controllers;

import app.SomeUtil;
import pwe.lang.*;

import java.util.HashMap;
import java.util.Map;

public class TestBasic {

    public static final Content simpleFunction(String arg1) {
        System.out.println("Executing controller with   argument " + arg1);

        return new TextContent(SomeUtil.transform("is it possible how awesome this is? I am a controller: " + arg1));

    }

    public static final Content simpleFunction4(String msg) {

        Map m = new HashMap();

        m.put("title", "my special title");

        return new TemplateHTMLContent("test4.ftl", m);
    }


    public static final Content simpleFunction5(String msg) {


        Map m = new HashMap();

        m.put("title", "my special title");

        return new TemplateHTMLContent("test2.ftl", m);

    }

    public static final Content testInternalLayout(){

        Map m = new HashMap();

        m.put("title", "my special title");

        return new TemplateHTMLContent("useInternalSimpleLayout.ftl", m);

    }

    public static final Content simpleFunction2(String msg, String message2) {

        Map<String,String> bindings = new HashMap<String,String>();

        bindings.put("title", "My Page");
        bindings.put("message", msg);

        return new TemplateHTMLContent("test1.ftl", bindings);

    }


    public static final Content simpleFunction3(ReadableValue<Integer> hitCount) {
        Map<String,String> bindings = new HashMap<String,String>();

        bindings.put("title", "My Page");
        bindings.put("hits", hitCount.getValue().toString());

        return new TemplateHTMLContent("test3.ftl", bindings);

    }

    }