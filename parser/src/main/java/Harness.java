import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pwe.lang.ExtractClassInfoListener;
import pwe.lang.JavaLexer;
import pwe.lang.JavaParser;

public class Harness {


    public static void main(String args[]) {
        // Parse our test file...
        parseFile("samples/TestBasic.java");

    }

    public static void parseFile(String f) {
        boolean gui = true;
        boolean printTree = true;

        try {
            System.out.print("Parsing file: " + f);

            // Create Lexer
            Lexer lexer = new JavaLexer(new ANTLRFileStream(f));

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            long start = System.currentTimeMillis();
            tokens.fill(); // load all and check time
            long stop = System.currentTimeMillis();
            System.out.println(String.format(" (lexed in %d ms)", stop - start));

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
            if (gui) t.inspect(parser);
//            if (printTree) System.out.println(t.toStringTree(parser));

            ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
            ExtractClassInfoListener extractor = new ExtractClassInfoListener(parser);
            walker.walk(extractor, t); // initiate walk of tree with listener


        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();   // so we can get stack trace
        }
    }
}
