package methods;

import pwe.lang.*;

public class ReadWrite{

     public static final void myFunction(WritableValue<Integer> hits){
          if(hits.getValue()==null){
              hits.setValue(0);
          }

         hits.setValue(hits.getValue()+1);
     }

    public static final void currentValue(ReadableValue<Integer> hits){
    }

}
