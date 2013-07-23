package methods;

import app.MyUtil;
import pwe.lang.WritableValue;

public class TestBasic {

    public static final void simpleFunction(String arg1) {
        System.out.println("Hey, you passed in the string: " + arg1);
    }

    public static final void simpleFunction2(String msg) {
    }

    public static final void simpleFunction3(WritableValue<Integer> hitCount) {
        if(hitCount.getValue()==null){
            hitCount.setValue(0);
        }

        if(1==1){
            System.out.println("test123");
        }

        hitCount.setValue(hitCount.getValue().intValue()+1);
    }

}