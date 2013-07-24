package core;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pwe.lang.*;
import pwe.lang.exceptions.TableHomomorphismException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Harness {


    final Logger logger = LoggerFactory.getLogger(Harness.class);

    private Path base;
    private static String controllersPath;
    private static String methodsPath;

    static {
        controllersPath = "controllers";
        methodsPath = "methods";
    }

    public static void main(String args[]) throws IOException, TableHomomorphismException {

        Harness h = new Harness(Paths.get(""));

        PwETable t = h.extractTranslationTable();
    }

    public Harness(Path base) {
        this.base = base;
    }

    public PwETable extractTranslationTable() throws IOException, TableHomomorphismException {

        // Parse out translation table from controllers
        PwETable controllerTable = new PwETable();

        DirectoryStream<Path> controllerFiles = Files.newDirectoryStream(base.resolve("src").resolve("main").resolve("java").resolve(controllersPath), "*.java");
        for (Path p : controllerFiles) {
            parseFile(p.toString(), controllerTable, PwEParserModes.PwEModeType.TYPE_CONTROLLER);
        }

        // Parse out translation table from methods
        PwETable methodTable = new PwETable();

        DirectoryStream<Path> methodFiles = Files.newDirectoryStream(base.resolve("src").resolve("main").resolve("java").resolve(methodsPath), "*.java");
        for (Path p : methodFiles) {
            parseFile(p.toString(), methodTable, PwEParserModes.PwEModeType.TYPE_METHOD);
        }

        // Start checking things

        //
        // Check translation table homomorphism
        //
        if (PwETable.fulfillsMeVCContractWith(controllerTable, methodTable) == false) {
            throw new TableHomomorphismException("Method and Controller tables do not match. For any given function in a method, there should be one matching in name, arity and type in your controllers.");
        }

        if(methodTable.hasMultipleMutations()==true){
            throw new TableHomomorphismException("You are attempting to modify your session in more than one place. Please ensure only one WritableValue per key is used.");
        }

        logger.info("The Following Methods Are Available to Your Application:");
        logger.info("\n{}", methodTable.asASCIITable());


        return methodTable;
    }

    public void parseFile(String f, PwETable table, PwEParserModes.PwEModeType mode) {
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

//
//            // start parsing at the compilationUnit rule
            ParserRuleContext t = parser.compilationUnit();
//
//            ParserRuleContext t = parser.compilationUnit();
////            if (notree) parser.setBuildParseTree(false);
//            if (gui) t.inspect(parser);
//            if (printTree) System.out.println(t.toStringTree(parser));

            ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
            ExtractClassInfoListener extractor = new ExtractClassInfoListener(parser, table, f, mode);
            walker.walk(extractor, t); // initiate walk of tree with listener


        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();   // so we can get stack trace
        }
    }
}
