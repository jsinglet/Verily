package verily.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author John L. Singleton <jsinglet@gmail.com>
 */

public class GMSClassInfoListener extends JavaBaseListener {



    final Logger logger = LoggerFactory.getLogger(GMSClassInfoListener.class);
    private GMSAccess.AccessType accessType = GMSAccess.AccessType.N;

    private JavaParser parser;
    private String filename;
    private Stack<String> classCtx = new Stack<String>();
    private List<GMSAccess> accessList = new LinkedList<GMSAccess>();
    private List<String> possibleSessionHolders = new LinkedList<String>();
    private List<String> imports = new LinkedList<String>();
    private String accessName = null;


    public GMSClassInfoListener(JavaParser parser, String filename) {
        this.parser = parser;
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
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        logger.info("{}Adding Import {}...", getDepth(), ctx.qualifiedName().getText());

        imports.add(ctx.qualifiedName().getText());
    }


    /*@Override
    public void enterStatementExpression(JavaParser.StatementExpressionContext ctx) {
        logger.info("{}enterStatementExpression: {}", getDepth(), ctx.getText());

        // if a session access type is turned on in our current context
        // we find the name of the variable we accessed.
        if(ctx.getText().contains("activePublic")){
            System.out.println("test");
        }


        if(accessType!=GMSAccess.AccessType.N){

            // we are either accessing a constant (literal) or a field of some sort.
            // either way we don't chase it down further.
            if(ctx.expression().expressionList().expression(0).getText().startsWith("\"")){
                accessName = ctx.expression().expressionList().expression(0).getText().substring(1, ctx.expression().expressionList().expression(0).getText().length()-1);
            }else{
                accessName = ctx.expression().expressionList().expression(0).getText();
            }
        }
    }*/
//
//    @Override
//    public void exitStatementExpression(JavaParser.StatementExpressionContext ctx) {
//    }


    /*@Override
    public void enterStatement(JavaParser.StatementContext ctx) {
        logger.info("{}enterStatement: {}", getDepth(), ctx.getText());

        // no access type initially
        accessType = GMSAccess.AccessType.N;
        accessName = null;

        // we are trying to write to the session
        for(String request : possibleSessionHolders){
            if(ctx.getText().startsWith(String.format("%s.getSession().setAttribute", request))){
                accessType = GMSAccess.AccessType.W;
                break;
            }
        }

        // we are trying to read from the session
        for(String request : possibleSessionHolders){
            if(ctx.getText().startsWith(String.format("%s.getSession().getAttribute", request))){
                accessType = GMSAccess.AccessType.R;
                break;
            }
        }
    }*/

    /*@Override
    public void exitStatement(JavaParser.StatementContext ctx) {

        // if we were capturing, add the captured result to our list.
        if(accessType!= GMSAccess.AccessType.N){
            logger.info("{}Recording GMS Access [{},{},{}]", getDepth(), filename, accessType, accessName);
            accessList.add(new GMSAccess(filename, accessType, accessName));
        }

        accessType = GMSAccess.AccessType.N;
        accessName = null;
    }
*/
    boolean isInSession = false;
    boolean captureGMSName = false;
    String method = null;
    Stack<JavaParser.ExpressionContext> exprStack = new Stack<JavaParser.ExpressionContext>();

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        logger.trace("{}Entering Expression: {}", getDepth(), ctx.getText());

        if(ctx.expression(0)!=null)
            logger.trace("{}Entering Expression(0): {}", getDepth(), ctx.expression(0).getText());


        if(captureGMSName){
            if(ctx.getText().startsWith("\"")){
                accessName = ctx.getText().substring(1, ctx.getText().length()-1);
            }else{
                accessName = ctx.getText();
            }
            captureGMSName = false;
        } else {

            // the node directly after this condition will be the session parameter
            if(ctx.expression(0)==null && exprIsSessionHolder(ctx)){
                isInSession = true;
                captureGMSName = true;
            }

        }

        exprStack.push(ctx);
    }

    @Override
    public void exitExpression(JavaParser.ExpressionContext ctx){

        if(ctx.expression(0)!=null && exprStack.size() <= 2 && exprStack.size() > 0 && isInSession){

            if(ctx.expression(0).getText().endsWith("setAttribute")){
                accessType = GMSAccess.AccessType.W;
            }else

            if(ctx.expression(0).getText().endsWith("getAttribute")){
                accessType = GMSAccess.AccessType.R;
            }else
                accessType = GMSAccess.AccessType.N;


            if(accessType!= GMSAccess.AccessType.N){
                logger.trace("{}{}", getDepth(), ctx.expression(0).getText());
                logger.info("{}Recording GMS Access [{},{},{}]", getDepth(), filename, accessType, accessName);
                getAccessList().add(new GMSAccess(filename, accessType, accessName, method, ctx.getStart().getLine()));

                isInSession = false;
            }

        }
        exprStack.pop();
    }

    public boolean exprIsSessionHolder(JavaParser.ExpressionContext expr){
        for(String request : possibleSessionHolders){
            if(request.equals(expr.getText())){
                return true;
            }
        }

        return false;
    }
    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        logger.info("{}Entering formal parameter context...", getDepth());
        logger.info("{}Found formal parameter \"{}\" with type \"{}\"", getDepth(), ctx.variableDeclaratorId().getText(), ctx.type().getText());

        // case 1, our imports already have the correct namespace and this is a HttpServletRequest
        if(hasNamespace() && ctx.type().getText().equals("HttpServletRequest")){
            possibleSessionHolders.add(ctx.variableDeclaratorId().getText());
        }


        // case 2, this parameter's type is fully qualified to javax.servlet.http.HttpServletRequest
        else if (ctx.type().getText().equals("javax.servlet.http.HttpServletRequest"))
        {
            possibleSessionHolders.add(ctx.variableDeclaratorId().getText());
        }

        // case 3, not an option.
        else
        {
            logger.info("{}Formal Parameter not a possible session holder...", getDepth());
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        logger.trace("{}Entering class method: {}", getDepth(), ctx.Identifier());
        method = ctx.Identifier().getText();
        possibleSessionHolders.clear();
    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        logger.trace("{}Exiting class method: {}", getDepth(), ctx.Identifier());
        method = null;
        possibleSessionHolders.clear();

    }

    private boolean hasNamespace(){
        for(String i : imports){
            if(i.equals("javax.servlet.http.HttpServletRequest") || i.equals("javax.servlet.http.*")){
                return true;
            }
        }
        return false;
    }

    public String getDepth() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < classCtx.size(); i++) {
            sb.append("\t\t");
        }
        return sb.toString();
    }

    public List<GMSAccess> getAccessList() {
        return accessList;
    }

    public void setAccessList(List<GMSAccess> accessList) {
        this.accessList = accessList;
    }
}
