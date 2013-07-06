import java.net.URL;

public class TestMain {

    public static void main(String args[]) {


        System.out.println("Getting resource:");

        URL u = TestMain.class.getResource("/");

        if (1 == 1)
            System.out.println("test");
    }
}
