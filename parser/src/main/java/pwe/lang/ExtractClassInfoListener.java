package pwe.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static pwe.lang.JavaParser.*;

public class ExtractClassInfoListener extends JavaBaseListener {

    final Logger logger = LoggerFactory.getLogger(ExtractClassInfoListener.class);
    private static final List<String> psfvReference = Arrays.asList("public", "static", "final", "void");
    private static final List<String> psfcReference = Arrays.asList("public", "static", "final", "Content");
    private PwEParserModes.PwEModeType mode;


    private JavaParser parser;
    private String filename;
    private PwETable et;
    private Stack<String> classCtx = new Stack<String>();
    private List<PwEType> methodSpec = new LinkedList<PwEType>();

    public ExtractClassInfoListener(JavaParser parser, PwETable et, String filename, PwEParserModes.PwEModeType mode) {
        this.parser = parser;
        this.et = et;
        this.filename = filename;
        this.mode = mode;
    }

    @Override
    public void enterClassDeclaration(ClassDeclarationContext ctx) {

        logger.trace("{}Descending into class {}...", getDepth(), ctx.Identifier());
        classCtx.push(ctx.Identifier().toString());
    }

    @Override
    public void exitClassDeclaration(ClassDeclarationContext ctx) {
        String formerCtx = classCtx.pop();
        logger.trace("{}Leaving class class {}...", getDepth(), formerCtx);
    }

    @Override
    public void enterFormalParameters(FormalParametersContext ctx) {
        logger.trace("{}Entering formal parameters context...", getDepth());
    }

    @Override
    public void enterFormalParameter(FormalParameterContext ctx) {

        logger.trace("{}Entering formal parameter context...{}", getDepth());
        logger.trace("{}Found formal parameter \"{}\" with type \"{}\"", getDepth(), ctx.variableDeclaratorId().getText(), ctx.type().getText());

        methodSpec.add(new PwEType(ctx.type().getText(), ctx.variableDeclaratorId().getText()));

    }

    @Override
    public void enterMethodDeclaration(MethodDeclarationContext ctx) {
        logger.trace("{}Parsing class method: {}", getDepth(), ctx.Identifier());
    }

    @Override
    public void exitMethodDeclaration(MethodDeclarationContext ctx) {

        logger.trace("{}Evaluating class method: {}", getDepth(), ctx.Identifier());

        if (methodHasNoLint() && baseSignatureIsValid(ctx) && currentClassIsFilename(ctx) && methodIsTopLevel(ctx)) {
            logger.trace("{}Discovered method \"{}\" will be mapped ➜ /{}/{}", getDepth(), ctx.Identifier(), classCtx.peek(), ctx.Identifier());

            et.mapMethod(classCtx.peek(), new PwEMethod(ctx.Identifier().getText(), methodSpec));

        } else {
            logger.trace("{}Discovered method \"{}\" not a candidate for PwE model.", getDepth(), ctx.Identifier());
        }

        methodSpec = new LinkedList<PwEType>();
    }

    private boolean baseSignatureIsValid(MethodDeclarationContext ctx){
        if(mode == PwEParserModes.PwEModeType.TYPE_METHOD){
            return methodIsPSFV(ctx);
        }

        return methodIsPSFC(ctx);
    }

    // check to see that this method is a DIRECT member of the class being parsed.
    private boolean methodIsTopLevel(MethodDeclarationContext ctx) {

        boolean isTL = classCtx.size() == 1;

        logger.trace("{}Checking to see if this is a top level function: [{}]", getDepth(), isTL);

        return isTL;
    }

    private boolean currentClassIsFilename(MethodDeclarationContext ctx) {
        String className = new File(filename).getName().split("\\.")[0];
        boolean ccif = className.equals(classCtx.peek());

        logger.trace("{}Checking to see if method's class matches the main class: [{}]", getDepth(), ccif);

        return ccif;
    }

    // checks to see that a method is public static final Content
    private boolean methodIsPSFC(MethodDeclarationContext ctx) {

        boolean isPSV = true; //false;

        // if this method is valid, the signature will be public static void, which will be
        // two levels up in the parser as follows
        // Note that ctx.type() == null when the return type is void
        if (ctx.type() != null && ctx.type().getText().equals(psfcReference.get(3)) && ctx.getParent() != null && ctx.getParent().getParent() != null) {

            if (ctx.getParent().getParent().children != null && ctx.getParent().getParent().children.size() == 4) {

                for (int i = 0; i < ctx.getParent().getParent().children.size() - 1; i++) {
                    if (ctx.getParent().getParent().children.get(i).getText().equals(psfcReference.get(i)) == false) {
                        isPSV = false;
                        break;
                    }
                }
            } else {
                isPSV = false;
            }
        } else {
            isPSV = false;
        }

        logger.trace("{}Checking to see if method is PSFC: [{}]", getDepth(), isPSV);
        return isPSV;
    }

    private boolean methodHasNoLint(){
        if(mode == PwEParserModes.PwEModeType.TYPE_CONTROLLER){ // don't allow writable values in the controller...



            for(PwEType t : methodSpec){
                if(t.isSessionWritable()){
                    logger.trace("{}Checking to see if this controller method tries to access WritableValues: [{}]", getDepth(), true);

                    return false;
                }
            }

            logger.trace("{}Checking to see if this controller method tries to access WritableValues: [{}]", getDepth(), false);

        }



        return true;
    }


    // checks to see that a method is public static final void
    private boolean methodIsPSFV(MethodDeclarationContext ctx) {

        boolean isPSV = true; //false;

        // if this method is valid, the signature will be public static void, which will be
        // two levels up in the parser as follows
        // Note that ctx.type() == null when the return type is void
        if (ctx.type() == null && ctx.getParent() != null && ctx.getParent().getParent() != null) {

            if (ctx.getParent().getParent().children != null && ctx.getParent().getParent().children.size() == 4) {

                for (int i = 0; i < ctx.getParent().getParent().children.size() - 1; i++) {
                    if (ctx.getParent().getParent().children.get(i).getText().equals(psfvReference.get(i)) == false) {
                        isPSV = false;
                        break;
                    }
                }
            } else {
                isPSV = false;
            }
        } else {
            isPSV = false;
        }

        logger.trace("{}Checking to see if method is PSFV: [{}]", getDepth(), isPSV);
        return isPSV;
    }

    public String getDepth() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < classCtx.size(); i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public PwEParserModes.PwEModeType getMode() {
        return mode;
    }

    public void setMode(PwEParserModes.PwEModeType mode) {
        this.mode = mode;
    }
}
