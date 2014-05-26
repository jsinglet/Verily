package core;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verily.lang.*;
import verily.lang.exceptions.TableHomomorphismException;
import verily.lang.util.MRRTableSet;
import verily.lang.util.TableDiffResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class JMLTransformationHarness {

    final Logger logger = LoggerFactory.getLogger(MRRHarness.class);

    private Path base;
    private static String routersPath;
    private static String methodsPath;

    static {
        routersPath = "routers";
        methodsPath = "methods";
    }

    //
    // Note to future explorers:
    //
    // To turn on more logging, manipulate the simplelogger system property like so:
    // -Dorg.slf4j.simpleLogger.defaultLogLevel=trace
    //
    public static void main(String args[]) throws IOException, TableHomomorphismException, JMLTransformationError {

        MRRHarness h = new MRRHarness(Paths.get(""));

        try {
            // extract the translation tables.
            MRRTableSet t = h.extractTranslationTable();

            // transform!
            JMLTransformationHarness t2 = new JMLTransformationHarness(Paths.get("").resolve(".verily").resolve("gen"));

            t2.transformToJMLCompatibleSource(t.getMethodTable(), t.getRouterTable());

        }catch(TableHomomorphismException e){

            System.err.println(e.getMessage());

            if(e.errorLocations!=null){
                for(TableDiffResult r : e.errorLocations){
                    System.err.println(r.toString());
                }
                if(e.errorLocations.size()==1){
                    System.err.println("1 error");
                }else{
                    System.err.println(String.format("\n%d errors", e.errorLocations.size()));
                }
            }
        }

    }

    public JMLTransformationHarness(Path base) {
        this.base = base;
    }

    public void toVerilyRTE(VerilyTable methodTable, VerilyTable routerTable) throws IOException {

        //Utils.assertionFailureL

        DirectoryStream<Path> methodFiles = Files.newDirectoryStream(base.resolve("src").resolve("main").resolve("java").resolve(methodsPath), "*.java");
        for (Path p : methodFiles) {

            String origFile = p.toString();

            String relative = new File(".verily/gen").toURI().relativize(p.toFile().toURI()).getPath();

            logger.info("[jml-to-verily] Transforming {}", relative);

            // extract the methods from the class.
            String transformed = TransformtoJMLRACListener.transformToVerily(routerTable, methodTable, p, relative);

            // rewrite the class
            File f = p.toFile();

            logger.info("[jml-to-verily] Writing {}", f.toString());
            PrintWriter writer = new PrintWriter(f, "UTF-8");
            writer.write(transformed);
            writer.close();

            // write out the needed support methods...

            // for each possible failure of a method, output
            //
            //
            // import import verily.lang.*;
            //
            // public class <MethodClassName>Validation {
            //
            //     public static final Content methodName(){
            //           return routers.<router clause>;
            //     }
            //

            for(String context : methodTable.getTable().keySet()){

                String newClazz = TransformtoJMLRACListener.toVerilySupportClasses(context, methodTable, p, relative);

                if(newClazz!=null){

                    Path newClazzp = base.resolve("src").resolve("main").resolve("java").resolve(methodsPath).resolve(context + "Validation.java");

                    logger.info("[jml-to-verily] Writing Runtime Checkers {}", newClazzp.toString());
                    PrintWriter w2 = new PrintWriter(newClazzp.toFile(), "UTF-8");
                    w2.write(newClazz);
                    w2.close();
                }

            }


        // write out the proxy classes

        for(String context : methodTable.getTable().keySet()){

            String newClazz = TransformtoJMLRACListener.toVerilyProxySupportClasses(context, methodTable, p, relative);

            if(newClazz!=null){

                Path newClazzp = base.resolve("src").resolve("main").resolve("java").resolve(methodsPath).resolve(context + "Proxy.java");

                logger.info("[jml-to-verily] Writing Runtime Proxy {}", newClazzp.toString());
                PrintWriter w2 = new PrintWriter(newClazzp.toFile(), "UTF-8");
                w2.write(newClazz);
                w2.close();
            }

        }
    }


    }

    public List<JMLTransformedSource> transformToJMLCompatibleSource(VerilyTable methodTable, VerilyTable routerTable) throws IOException, JMLTransformationError {

        // should return a list of method signatures to generate.

        DirectoryStream<Path> methodFiles = Files.newDirectoryStream(base.resolve("src").resolve("main").resolve("java").resolve(methodsPath), "*.java");
        for (Path p : methodFiles) {

            String origFile = p.toString();

            String relative = new File(".verily/gen").toURI().relativize(p.toFile().toURI()).getPath();

            logger.info("[jml-transform] Transforming {}", relative);

            // extract the methods from the class.
            String transformed = TransformtoJMLRACListener.stripNonJML(routerTable, methodTable, p, relative);

            // rewrite the class
            File f = p.toFile();

            logger.info("[jml-transform] Writing {}", f.toString());
            PrintWriter writer = new PrintWriter(f, "UTF-8");
            writer.write(transformed);
            writer.close();

        }




        return null;
    }




    public void parseFile(String f, VerilyTable methods, VerilyTable routers) {
        boolean gui = true;
        boolean printTree = true;

        try {
            logger.trace("Parsing file: {}", f);

            // Create Lexer
            Lexer lexer = new JavaLexer(new ANTLRFileStream(f));

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            long start = System.currentTimeMillis();
            tokens.fill(); // load all and check time
            long stop = System.currentTimeMillis();
            logger.trace(String.format("File [%s] (lexed in %d ms)", f, stop - start));

            // Create a parser that reads from the scanner
            JavaParser parser = new JavaParser(tokens);

//            if (diag) parser.addErrorListener(new DiagnosticErrorListener());
//            if (bail) parser.setErrorHandler(new BailErrorStrategy());
//            if (SLL) parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

            System.out.println(parser.compilationUnit().getText());
//
//            // start parsing at the compilationUnit rule
            ParserRuleContext t = parser.compilationUnit();
//
//            ParserRuleContext t = parser.compilationUnit();
////            if (notree) parser.setBuildParseTree(false);
//            if (gui) t.inspect(parser);
//            if (printTree) System.out.println(t.toStringTree(parser));

            System.out.println(tokens.getText());

            ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
            TransformtoJMLRACListener extractor = new TransformtoJMLRACListener(parser, methods, routers, f);
            walker.walk(extractor, t); // initiate walk of tree with listener


        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();   // so we can get stack trace
        }
    }
}
