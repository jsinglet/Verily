package methods;

import app.MyUtil;
import verily.lang.WritableValue;

public class TestBasic {

    public static final void simpleFunction(String arg1) {

        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println("Hey, you passed in the sdfsdfstring: " + arg1);
    }

    public static final void simpleFunction2(String msg, String message2) {
    }

    public static final void simpleFunction4(String msg) {

    }


    public static final void testInternalLayout(){}

    public static final void simpleFunction5(String msg) {

    }


    public static final void simpleFunction3(WritableValue<Integer> hitCount) {
        if(hitCount.getValue()==null){
            hitCount.setValue(0);
        }

        if(1==1){
            System.out.println("test123ss");
        }

        hitCount.setValue(hitCount.getValue().intValue()+1);
    }

}