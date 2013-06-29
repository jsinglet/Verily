package pwe.lang;

import org.antlr.v4.runtime.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ExtractClassInfoListener extends JavaBaseListener {

    final Logger logger = LoggerFactory.getLogger(ExtractClassInfoListener.class);

    JavaParser parser;

    private Stack<String> classCtx = new Stack<String>();
    private Queue<PwEType> methodSpec = new LinkedList<PwEType>();

    public ExtractClassInfoListener(JavaParser parser) {
        this.parser = parser;
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
        logger.info("{}Entering formal parameters context...{}", getDepth());
    }

    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        logger.info("{}Entering formal parameter context...{}", getDepth());
        logger.info("{}Found formal parameter \"{}\" with type \"{}\"", getDepth(), ctx.variableDeclaratorId().getText(), ctx.type().getText());

        methodSpec.add(new PwEType(ctx.type().getText(), ctx.variableDeclaratorId().getText()));

    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        // need parser to get tokens
        TokenStream tokens = parser.getTokenStream();
        String type = "void";
        if (ctx.type() != null) {
            type = tokens.getText(ctx.type());
        }

        //ctx.formalParameters().formalParameterList().children();

        String args = tokens.getText(ctx.formalParameters());
        System.out.println("\t" + type + " " + ctx.Identifier() + args + ";");

        logger.info("{}Parsing class method: {}", getDepth(), ctx.Identifier());

    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {

        logger.info("{}Evaluating class method: {}", getDepth(), ctx.Identifier());

        if (methodIsPSV(ctx)) {
            logger.info("{}Discovered method \"{}\" will be mapped => /{}/{}", getDepth(), ctx.Identifier(), classCtx.peek(), ctx.Identifier());

        } else {
            logger.info("{}Discovered method \"{}\" not a candidate for PwE model.", getDepth(), ctx.Identifier());
        }

        methodSpec.clear();
    }


    // checks to see that a method is public static void
    public boolean methodIsPSV(JavaParser.MethodDeclarationContext ctx) {

        boolean isPSV = true; //false;


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
