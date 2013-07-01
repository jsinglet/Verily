package pwe.lang;

import org.antlr.v4.runtime.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ExtractClassInfoListener extends JavaBaseListener {

    final Logger logger = LoggerFactory.getLogger(ExtractClassInfoListener.class);

    private JavaParser parser;
    private String filename;
    private PwETable et;
    private Stack<String> classCtx = new Stack<String>();
    private List<PwEType> methodSpec = new LinkedList<PwEType>();

    public ExtractClassInfoListener(JavaParser parser, PwETable et, String filename) {
        this.parser = parser;
        this.et = et;
        this.filename = filename;
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {

        logger.info("{}Descending into class {}...", getDepth(), ctx.Identifier());
        classCtx.push(ctx.Identifier().toString());
    }

    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String formerCtx = classCtx.pop();
        logger.info("{}Leaving class class {}...", getDepth(), formerCtx);
    }

    @Override
    public void enterFormalParameters(JavaParser.FormalParametersContext ctx) {
        logger.info("{}Entering formal parameters context...", getDepth());
    }

    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {

        logger.info("{}Entering formal parameter context...{}", getDepth());
        logger.info("{}Found formal parameter \"{}\" with type \"{}\"", getDepth(), ctx.variableDeclaratorId().getText(), ctx.type().getText());

        methodSpec.add(new PwEType(ctx.type().getText(), ctx.variableDeclaratorId().getText()));

    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        logger.info("{}Parsing class method: {}", getDepth(), ctx.Identifier());
    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {

        logger.info("{}Evaluating class method: {}", getDepth(), ctx.Identifier());

        if (methodIsPSV(ctx) && currentClassIsFilename(ctx) && methodIsTopLevel(ctx)) {
            logger.info("{}Discovered method \"{}\" will be mapped => /{}/{}", getDepth(), ctx.Identifier(), classCtx.peek(), ctx.Identifier());

            et.mapMethod(classCtx.peek(), new PwEMethod(ctx.Identifier().getText(), methodSpec));

        } else {
            logger.info("{}Discovered method \"{}\" not a candidate for PwE model.", getDepth(), ctx.Identifier());
        }

        methodSpec.clear();
    }

    // check to see that this method is a DIRECT member of the class being parsed.
    private boolean methodIsTopLevel(JavaParser.MethodDeclarationContext ctx) {

        boolean isTL = classCtx.size() == 1;

        logger.info("{}Checking to see if this is a top level function: [{}]", getDepth(), isTL);

        return isTL;
    }

    private boolean currentClassIsFilename(JavaParser.MethodDeclarationContext ctx) {
        String className = new File(filename).getName().split("\\.")[0];
        boolean ccif = className.equals(classCtx.peek());

        logger.info("{}Checking to see if method's class matches the main class: [{}]", getDepth(), ccif);

        return ccif;
    }

    // checks to see that a method is public static void
    private boolean methodIsPSV(JavaParser.MethodDeclarationContext ctx) {

        boolean isPSV = true; //false;


        TokenStream tokens = parser.getTokenStream();
        String type = "void";
        if (ctx.type() != null) {
            type = tokens.getText(ctx.type());
        }

        String args = tokens.getText(ctx.formalParameters());
        System.out.println("\t" + type + " " + ctx.Identifier() + args + ";");


        logger.info("{}Checking to see if method is PSV: [{}]", getDepth(), isPSV);
        return isPSV;
    }

    public String getDepth() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < classCtx.size(); i++) {
            sb.append("\t");
        }
        return sb.toString();
    }
}
