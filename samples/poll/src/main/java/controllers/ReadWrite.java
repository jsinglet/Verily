package controllers;

import pwe.lang.*;

public class ReadWrite{

     public static final Content myFunction(ReadableValue<Integer> hits){
          return new TextContent("New Value: " + hits.getValue());
     }


    public static final Content currentValue(ReadableValue<Integer> hits){
        return new TextContent("New Value: " + hits.getValue());
    }
}
