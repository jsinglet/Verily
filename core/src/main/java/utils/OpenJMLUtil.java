package utils;

import core.VerilyContainer;
import exceptions.VerilyCompileFailedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.openjml.Prover;
import utils.openjml.ProverConfigurationException;
import utils.openjml.Strings;
import utils.openjml.Z3Prover;
import utils.openjml.ui.ConfigureSMTProversDialog;
import utils.openjml.ui.MessageUtil;
import utils.openjml.ui.res.ApplicationMessages;
import verily.lang.VerilyParserModes;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class OpenJMLUtil {

    protected final static Logger logger = LoggerFactory.getLogger(OpenJMLUtil.class);


    public static String genDir = ".verily/gen/src";

    public static void preJML() throws IOException {
        // create a properties file for openjml
        configureProvers();

    }

    private static List<String> jarsInProject() throws IOException {

        List<String> jars = new ArrayList<String>();

        Path base = VerilyContainer.getContainer().getEnv().getHome();

        DirectoryStream<Path> depJars  = Files.newDirectoryStream(base.resolve("target").resolve("dependency"), "*.jar");
        for (Path p : depJars) {
            jars.add(p.toString());
        }

        return jars;
    }

    public static String pathToJMLJar(){
        return VerilyContainer.getContainer().getEnv().getJmlHome() + File.separator + "openjml.jar";
    }

    public static String pathToJMLESCJar(){
        return VerilyContainer.getContainer().getEnv().getJmlHome() + "-head" + File.separator + "openjml.jar";
    }


    private static String[] getESCCommandArgs() throws IOException {

        List<String> args = new ArrayList<String>();
        List<String> jars = jarsInProject();

        args.add("java");
        args.add("-jar");
        args.add(pathToJMLESCJar());
        args.add("-esc");
        args.add("-classpath");
        args.add("\"" + StringUtils.join(jars, File.pathSeparator) + "\"");
        args.add("-dir");
        args.add(".verily/gen/src/main/java/");

        String[] ar = new String[args.size()];
        return args.toArray(ar);

    }


    private static String[] getRacCommandArgs() throws IOException {

        List<String> args = new ArrayList<String>();
        List<String> jars = jarsInProject();

        args.add("java");
        args.add("-jar");
        args.add(pathToJMLJar());
        args.add("-rac");
        args.add("-classpath");
        args.add("\"" + StringUtils.join(jars, File.pathSeparator) + "\"");
        args.add("-show");
        args.add("-d");
        args.add(".verily/out/");
        args.add("-dir");
        args.add(".verily/gen/src/main/java/methods");

        String[] ar = new String[args.size()];
        return args.toArray(ar);

    }

    public static boolean racOutputToFiles(List<String> racOutput) throws FileNotFoundException, UnsupportedEncodingException {

        if(racOutput.size()==0 || racOutput.get(0).startsWith("[jmlrac")==false){
            return false;
        }

        logger.info("Saving RAC Generated Files...");

        Iterator<String> it = racOutput.iterator();

        while(it.hasNext()){

            String l = it.next();
            // output this file.
            if(l.startsWith("[jmlrac] RAC Transformed")){
                // get the filename
                String filename = l.replace("[jmlrac] RAC Transformed: ", "");

                File f = new File(filename);
                logger.info("[jmlrac] Writing " + filename);
                PrintWriter writer = new PrintWriter(f, "UTF-8");

                l = it.next();

                while(l.startsWith("[jmlrac")){

                    int closePos = l.indexOf(']');
                    String substr = l.substring(closePos+1);

                    String line = substr.replace("    " , "");

                    writer.write(line + System.getProperty("line.separator"));

                    l = it.next();

                }

                writer.close();

            }
        }

        return true;
    }


    public static String escCompileProject() throws IOException, InterruptedException, VerilyCompileFailedException {

        Process p;

        p = new ProcessBuilder(getESCCommandArgs()).redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        List<String> escOutput = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ( (line = br.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
            escOutput.add(line);
        }

        is.close();

        int exitStatus = p.waitFor();



        if (exitStatus != 0 || (escOutput.size() > 0 && escOutput.get(escOutput.size()-1).contains("warnings"))) {

            for(String l : escOutput){
                if(l.startsWith(".verily"))
                    logger.error(l.substring(12)); // hides the fact that these are generated files...
                else
                    logger.error(l);
            }
            throw new VerilyCompileFailedException("ESC checking failed. Please see output for details.");
        }

        return sb.toString();

    }

    public static String racCompileProject() throws IOException, InterruptedException, VerilyCompileFailedException {


        // java -jar "C:\Program Files\Verily\tools\openjml\openjml.jar" -rac -classpath "C:\Program Files\Verily\lib\core-1.0-SNAPSHOT.jar" -show -d .verily/out -dir src\main\java


        Process p;

        p = new ProcessBuilder(getRacCommandArgs()).redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        List<String> racOutput = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ( (line = br.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
            racOutput.add(line);
        }

        is.close();

        int exitStatus = p.waitFor();


        // because of a JML bug this doesn't work!!

//        if (exitStatus != 0) {
//            throw new VerilyCompileFailedException("Building project failed. Please see output for details.");
//        }


        boolean ok = racOutputToFiles(racOutput);

        if(!ok){
            for(String l : racOutput){
                if(l.startsWith(".verily"))
                    logger.error(l.substring(12)); // hides the fact that these are generated files...
                else
                    logger.error(l);
            }

            throw new VerilyCompileFailedException("JML Rac Translation Failed.");
        }

        return sb.toString();
    }




    public static File configureProvers() throws IOException {

        logger.info(MessageUtil
                .getMessage(ApplicationMessages.ApplicationMessageKey.MsgStartingConfiguration));

        Prover p;

        //
        // Z3 is all we support for now.
        //
        if(System.getProperty("os.name").startsWith("Windows"))
            p = new Z3Prover(VerilyContainer.getContainer().getEnv().getZ3Home() + "\\bin\\z3.exe");
        else
            p = new Z3Prover(VerilyContainer.getContainer().getEnv().getZ3Home() + "/bin/z3");


        Properties properties = new Properties();

        //
        // Two kinds of persistence may be requested, USER, and PROJECT.
        //
        File existingFile = getProjectPropertiesFile();

        //
        // If the selected destination file exists, merge the existing settings
        //
        if(existingFile.exists()){
            FileInputStream fis = new FileInputStream(existingFile);
            properties.load(fis);
            fis.close();
        }

        //
        // This is the minimal set of settings required for the static checker.
        //
        properties.setProperty(Strings.defaultProverProperty,p.getPropertiesName());
        properties.setProperty(Strings.proverPropertyPrefix + p.getPropertiesName(), p.getExecutable());

        //
        // Write it out to an options file
        //

        FileOutputStream os = new FileOutputStream(existingFile);
        properties.store(os, "This file was generated by the Verily Web Framework.");

        os.close();

        return existingFile;
    }


    /**
     * Gets the openjml.properties file in the user's home directory.
     *
     * @return A new File object pointing to the openjml.properties file in the user's home directory.
     */
    public static File getUserPropertiesFile()
    {
        return new File(System.getProperty("user.home") + "/" + Strings.propertiesFileName);
    }

    /**
     * Gets the openjml.properties file in the current working directory.
     *
     * @return A new File object pointing to the openjml.properties file in the current working directory.
     */
    public static File getProjectPropertiesFile()
    {
        return new File(System.getProperty("user.dir") + "/" + Strings.propertiesFileName);
    }
    /** Finds OpenJML properties files in pre-defined places, reading their
     * contents and loading them into the System property set.
     */
    public static Properties findProperties() {

        Properties properties = System.getProperties();
        // Load properties files found in these locations:
        // These are read in inverse order of priority, so that later reads
        // overwrite the earlier ones.


        // In the user's home directory
        {
            File f = getUserPropertiesFile();
            try {
                readProps(properties,f);
                logger.trace("Properties read from user's home directory: " + f.getAbsolutePath());

            } catch (java.io.IOException e) {
                logger.trace("Failed to read property file " + f.getAbsolutePath());
            }
        }

        // In the working directory
        {
            File f = getProjectPropertiesFile();
            try {
                readProps(properties,f);

               logger.trace("Properties read from working directory: " + f.getAbsolutePath());

            } catch (java.io.IOException e) {
                logger.trace("Failed to read property file " + f.getAbsolutePath());
            }
        }


        return properties;
    }

    /**
     * Reads the properties into the given properties object.
     *
     * @param properties The properties to read into
     * @param f The file to read from
     * @throws java.io.IOException Thrown if the file can't be found or can't be read.
     */
    public static void readProps(Properties properties, File f) throws java.io.IOException {
        properties.load(new FileInputStream(f));
    }

}
