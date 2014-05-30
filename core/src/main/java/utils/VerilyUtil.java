package utils;

import core.VerilyContainer;
import exceptions.InvalidFormalArgumentsException;
import exceptions.VerilyCompileFailedException;
import org.apache.commons.io.FileUtils;
import verily.lang.VerilyType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class VerilyUtil {

    private static final ResourceBundle msgResources;
    private static final Map<String, Class> mappedTypes;


    static {
        msgResources = ResourceBundle.getBundle("content.ApplicationMessages", Locale.getDefault());

        mappedTypes = new HashMap<String, Class>();


        mappedTypes.put("int", Integer.TYPE);
        mappedTypes.put("long", Long.TYPE);
        mappedTypes.put("short", Short.TYPE);
        mappedTypes.put("boolean", Boolean.TYPE);

        mappedTypes.put("byte", Byte.TYPE);
        mappedTypes.put("float", Float.TYPE);
        mappedTypes.put("double", Double.TYPE);
        mappedTypes.put("char", Character.TYPE);

    }

    public static String getMessage(String key) {
        return msgResources.getString(key);
    }

    // copy everything over to the correct source directory
    public static void prepareForAnalysis() throws IOException {

        // ignore if this fails
        try {
            FileUtils.forceDelete(new File(".verily"));
        }catch(Exception e){}

        // need these.
        FileUtils.forceMkdir(new File(".verily/gen/src/"));
        FileUtils.forceMkdir(new File(".verily/out"));


        FileUtils.copyDirectory(new File("src"), new File(".verily/gen/src"), true);

    }

    public static void compileProject() throws IOException, InterruptedException, VerilyCompileFailedException {

        Process p;

        if(System.getProperty("os.name").startsWith("Windows"))
            p = new ProcessBuilder("mvn.bat", "package").redirectErrorStream(true).start();
        else
            p = new ProcessBuilder("mvn", "package").redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        int c = isr.read();

        while (c != -1) {
            System.out.write(c);
            c = isr.read();
        }

        is.close();

        int exitStatus = p.waitFor();


        if (exitStatus != 0) {
            throw new VerilyCompileFailedException("Building project failed. Please see output for details.");
        }
    }

    public static void reloadProject() throws IOException, InterruptedException, VerilyCompileFailedException {

        Process p;

        if(System.getProperty("os.name").startsWith("Windows"))
            p = new ProcessBuilder("mvn.bat", "compile", "-q").redirectErrorStream(true).start();
        else
            p = new ProcessBuilder("mvn", "compile", "-q").redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        int c = isr.read();

        while (c != -1) {
            System.out.write(c);
            c = isr.read();
        }

        is.close();

        int exitStatus = p.waitFor();


        if (exitStatus != 0) {
            throw new VerilyCompileFailedException("Building project failed. Please see output for details.");
        }
    }

    public static void test() throws IOException, InterruptedException, VerilyCompileFailedException {

        Process p;

        if(System.getProperty("os.name").startsWith("Windows"))
            p = new ProcessBuilder("mvn.bat", "test").redirectErrorStream(true).start();
        else
            p = new ProcessBuilder("mvn", "test").redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        int c = isr.read();

        while (c != -1) {
            System.out.write(c);
            c = isr.read();
        }

        is.close();

        int exitStatus = p.waitFor();


        if (exitStatus != 0) {
            throw new VerilyCompileFailedException("One or more tests failed. Please see output for details.");
        }
    }

    public static String trimRequestContext(String ctx) {
        if (ctx != null) {

            int idx1, idx2;

            // find the first slash
            for (idx1 = 0; idx1 < ctx.length() && ctx.charAt(idx1) == '/'; idx1++) ;

            // no slashes
            if (idx1 == ctx.length()) {
                return ctx;
            }

            // find the first slash
            for (idx2 = ctx.length() - 1; idx2 > idx1 && ctx.charAt(idx2) == '/'; idx2--) ;

            return ctx.substring(idx1, idx2 + 1);
        }

        return ctx;
    }

    public static String mimeForType(URL file) {
        // this method isn't super reliable so we are going to do it the hard way
        //return URLConnection.guessContentTypeFromName(file.getFile());

        if(file.getFile().endsWith(".css")){
            return "text/css";
        }
        if(file.getFile().endsWith(".js")){
            return "text/javascript";
        }
        if(file.getFile().endsWith(".png")){
            return "image/png";
        }
        if(file.getFile().endsWith(".gif")){
            return "image/gif";
        }
        if(file.getFile().endsWith(".jpg")){
            return "image/jpeg";
        }
        if(file.getFile().endsWith(".jpeg")){
            return "image/jpeg";
        }
        if(file.getFile().endsWith(".pdf")){
            return "application/pdf";
        }



        return "text/html";
    }


    public static Object coerceToType(VerilyType type, String guess) throws InvalidFormalArgumentsException {

        if (guess == null) {
            return guess;
        }

        String s = guess.trim();

        try {
            if (type.getType().equals("Integer") || type.getType().equals("int")) {
                return Integer.parseInt(s);
            } else if(type.getType().equals("Double") || type.getType().equals("double")){
                return Double.parseDouble(s);
            }else if (type.getType().equals("Boolean") || type.getType().equals("boolean")) {

                if (s.equalsIgnoreCase("true") || s.equals("1"))
                    return new Boolean(true);
                else if (s.equalsIgnoreCase("false") || s.equals("0"))
                    return new Boolean(false);
                else
                    throw new InvalidFormalArgumentsException(String.format("Cannot convert actual parameter value \"%s\" to a Boolean.", s));
            } else if (type.getType().equals("String")) {
                return s;
            } else {
                // TODO: Implement other data types
                throw new InvalidFormalArgumentsException(String.format("Cannot construct requested type \"%s\" because it is not yet implemented.", type.getType()));
            }

        } catch (Exception e) {
            throw new InvalidFormalArgumentsException(String.format("Cannot convert actual parameter value \"%s\" to a %s.", s, type.getType()));
        }
    }

    public static Class translatedType(VerilyType t, Class o) {

        if (mappedTypes.get(t.getType()) != null) {
            return mappedTypes.get(t.getType());
        }


        return o;
    }

    public static void reloadProjectFromGen() throws IOException, InterruptedException, VerilyCompileFailedException {

        // step 1, copy the target directory and the pom file to .verily/gen
        FileUtils.copyDirectoryToDirectory(new File("target"), new File(".verily/gen/"));

        FileUtils.copyFileToDirectory(new File("pom.xml"), new File(".verily/gen/"));

        FileUtils.copyDirectoryToDirectory(new File("lib"), new File(".verily/gen/"));

        // run the maven reload

        //mvn -f .verily/gen/pom.xml compile

        Process p;

        if(System.getProperty("os.name").startsWith("Windows"))
            p = new ProcessBuilder("mvn.bat", "-f", ".verily\\gen\\pom.xml", "compile", "-q").redirectErrorStream(true).start();
        else
            p = new ProcessBuilder("mvn", "-f", ".verily/gen/pom.xml", "compile", "-q").redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        int c = isr.read();

        while (c != -1) {
            System.out.write(c);
            c = isr.read();
        }

        is.close();

        int exitStatus = p.waitFor();


        if (exitStatus != 0) {
            throw new VerilyCompileFailedException("Building project failed. Please see output for details.");
        }

        //copy it all back.
        FileUtils.copyDirectoryToDirectory(new File(".verily/gen/target/classes"), new File("target/"));
    }
}
