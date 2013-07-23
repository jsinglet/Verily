package reification;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureClassLoader;

public class PwEClassLoader extends ClassLoader {


    public PwEClassLoader(ClassLoader parent) {
        super(parent);
    }

    public PwEClassLoader() {
        super();
    }

    public Class<?> loadClass(final String theClass) throws ClassNotFoundException {


        System.out.println("Finding class: " + theClass);


        // we want to dynamically load these types of classes
        if (theClass.startsWith("methods.") || theClass.startsWith("controllers.") || theClass.startsWith("app.")) {

            URLClassLoader loader = null;
            try {
                loader = new URLClassLoader(new URL[]{getClassPath()}) {
                    public Class loadClass(String name) throws ClassNotFoundException {
                        if (theClass.equals(name))
                            return findClass(name);
                        return super.loadClass(name);
                    }
                };
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return loader.loadClass(theClass);


        } else {
            return super.findClass(theClass);
        }


    }


    public static URL getClassPath() throws MalformedURLException {

        Path p = Paths.get("").resolve("target").resolve("classes");

        return p.toUri().toURL();
    }



}
