import pwe.lang.Content;
import reification.PwEClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.*;

public class TestMain {

    public static void main(String args[]) throws InterruptedException, IOException {


//        System.out.println("Getting resource:");
//
//        URL u = TestMain.class.getResource("/resources/");
//
//        if (1 == 1)
//            System.out.println("test");


        WatchService ws = FileSystems.getDefault().newWatchService();

        Path p = Paths.get("").resolve("src").resolve("main").resolve("java");

        p.register(ws, StandardWatchEventKinds.ENTRY_MODIFY);


        WatchKey key;
        try {
            key = ws.take();

            if(1==1){
                System.out.println("test");
            }

        } catch (InterruptedException x) {
            return;
        }


//        TestMain tm = new TestMain();
//
//        tm.foo();
//        Thread.sleep(30000L);
//        tm.foo();
    }


    public void foo() {


        try {
            Class c = Class.forName(String.format("methods.%s", "TestBasic"), false, new PwEClassLoader(TestMain.class.getClassLoader()));
            c.getMethod("doIt", null).invoke(null, null);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }
}
