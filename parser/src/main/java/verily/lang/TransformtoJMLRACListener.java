package verily.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


import static verily.lang.JavaParser.*;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class TransformtoJMLRACListener extends JavaBaseListener {

    final Logger logger = LoggerFactory.getLogger(TransformtoJMLRACListener.class);
    /* Deprecated - void is no longer a requirement */
    private static final List<String> psfvReference = Arrays.asList("public", "static", "final", "void");
    private static final List<String> psfcReference = Arrays.asList("public", "static", "final", "Content");
    private VerilyParserModes.VerilyModeType mode;


    private JavaParser parser;
    private String filename;
    private VerilyTable et;
    private Stack<String> classCtx = new Stack<String>();
    private List<VerilyType> methodSpec = new LinkedList<VerilyType>();

    public TransformtoJMLRACListener(JavaParser parser, VerilyTable et, String filename, VerilyParserModes.VerilyModeType mode) {
        this.parser = parser;
        this.et = et;
        this.filename = filename;
        this.mode = mode;
    }

    @Override
    public void enterClassDeclaration(ClassDeclarationContext ctx) {

        logger.trace("Descending into class {}...", ctx.Identifier());
        classCtx.push(ctx.Identifier().toString());
    }

    @Override
    public void exitClassDeclaration(ClassDeclarationContext ctx) {
        String formerCtx = classCtx.pop();
        logger.trace("Leaving class class {}...", formerCtx);
    }

}
