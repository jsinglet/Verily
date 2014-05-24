package verily.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static verily.lang.JavaParser.*;

public class ExtractClassInfoListener extends JavaBaseListener {

    final Logger logger = LoggerFactory.getLogger(ExtractClassInfoListener.class);
    /* Deprecated - void is no longer a requirement */
    private static final List<String> psfvReference = Arrays.asList("public", "static", "final", "void");
    private static final List<String> psfcReference = Arrays.asList("public", "static", "final", "Content");
    private VerilyParserModes.VerilyModeType mode;


    private JavaParser parser;
    private String filename;
    private VerilyTable et;
    private Stack<String> classCtx = new Stack<String>();
    private List<VerilyType> methodSpec = new LinkedList<VerilyType>();

    public ExtractClassInfoListener(JavaParser parser, VerilyTable et, String filename, VerilyParserModes.VerilyModeType mode) {
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

        methodSpec.add(new VerilyType(ctx.type().getText(), ctx.variableDeclaratorId().getText()));

    }

    @Override
    public void enterMethodDeclaration(MethodDeclarationContext ctx) {
        logger.trace("{}Parsing class method: {}", getDepth(), ctx.Identifier());
    }

    @Override
    public void exitMethodDeclaration(MethodDeclarationContext ctx) {

        int maybeLineNumber = ctx.getStart().getLine();

        logger.trace("{}Evaluating class method: \"{}\" on line {}", getDepth(), ctx.Identifier(), maybeLineNumber);

        if (methodHasNoLint() && baseSignatureIsValid(ctx) && currentClassIsFilename(ctx) && methodIsTopLevel(ctx)) {
            logger.trace("{}Discovered method {}:{} will be mapped ➜ /{}/{}", getDepth(), ctx.Identifier(), maybeLineNumber, classCtx.peek(), ctx.Identifier());

            et.mapMethod(classCtx.peek(), new VerilyMethod(ctx.Identifier().getText(), methodSpec, ctx.type(), maybeLineNumber));

        } else {
            logger.trace("{}Discovered method \"{}\" not a candidate for Verily.", getDepth(), ctx.Identifier());
        }

        methodSpec = new LinkedList<VerilyType>();
    }

    private boolean baseSignatureIsValid(MethodDeclarationContext ctx){
        if(mode == VerilyParserModes.VerilyModeType.TYPE_METHOD){
            return methodIsValidMethodFormat(ctx);
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
    //
    // TODO: In the future, maybe do some more parsing to determine what the
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
        if(mode == VerilyParserModes.VerilyModeType.TYPE_ROUTER){ // don't allow writable values in the controller...



            for(VerilyType t : methodSpec){
                if(t.isSessionWritable()){
                    logger.trace("{}Checking to see if this controller method tries to access WritableValues: [{}]", getDepth(), true);

                    return false;
                }
            }

            logger.trace("{}Checking to see if this controller method tries to access WritableValues: [{}]", getDepth(), false);

        }



        return true;
    }


    // checks to see that a method is public static final *
    private boolean methodIsValidMethodFormat(MethodDeclarationContext ctx) {

        boolean isVMF = true; //false;

        // if this method is valid, the signature will be public static *, which will be
        // two levels up in the parser as follows
        // Note that ctx.type() == null when the return type is void
        if (ctx.getParent() != null && ctx.getParent().getParent() != null) {

            if (ctx.getParent().getParent().children != null && ctx.getParent().getParent().children.size() == 4) {

                for (int i = 0; i < ctx.getParent().getParent().children.size() - 2; i++) {
                    if (ctx.getParent().getParent().children.get(i).getText().equals(psfvReference.get(i)) == false) {
                        isVMF = false;
                        break;
                    }
                }
            } else {
                isVMF = false;
            }
        } else {
            isVMF = false;
        }

        logger.trace("{}Checking to see if method is VMF: [{}]", getDepth(), isVMF);
        return isVMF;
    }

    public String getDepth() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < classCtx.size(); i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public VerilyParserModes.VerilyModeType getMode() {
        return mode;
    }

    public void setMode(VerilyParserModes.VerilyModeType mode) {
        this.mode = mode;
    }
}
