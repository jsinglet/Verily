package verily.lang;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verily.lang.exceptions.MethodNotMappedException;

import java.io.*;
import java.nio.file.Path;
import java.util.*;


import static verily.lang.JavaParser.*;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class TransformtoJMLRACListener extends JavaBaseListener {

    final Logger logger = LoggerFactory.getLogger(TransformtoJMLRACListener.class);


    private JavaParser parser;
    private String filename;
    private VerilyTable methods;
    private VerilyTable routers;

    private Stack<String> classCtx = new Stack<String>();
    private List<VerilyType> methodSpec = new LinkedList<VerilyType>();

    public TransformtoJMLRACListener(JavaParser parser, VerilyTable methods, VerilyTable routers, String filename) {
        this.parser = parser;
        this.methods = methods;
        this.routers = routers;
        this.filename = filename;
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

    public static String toVerilySupportClasses(String context, VerilyTable methods, Path p, String rel) throws IOException {

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


        boolean hasHandlers = false;

        StringBuffer sb = new StringBuffer();

        sb.append("import verily.lang.*;");
        sb.append(System.getProperty("line.separator"));
        sb.append(String.format("public class %sValidation {", context));
        sb.append(System.getProperty("line.separator"));

        for(VerilyMethod method : methods.getTable().get(context).values()){
            if(method.getOnFailClause()!=null){

                List<String> formalParams = new ArrayList<String>();

                for(VerilyType t : method.getFormalParameters()){
                    formalParams.add(t.toString());
                }

                sb.append(String.format("public static final Content %s(%s) {", method.getMethod(), StringUtils.join(formalParams, ", ")));
                sb.append(System.getProperty("line.separator"));
                sb.append(String.format("\treturn routers.%s.%s;", context, method.getOnFailClause()));
                sb.append(System.getProperty("line.separator"));
                sb.append("}");

                hasHandlers = true;
            }
        }

        sb.append(System.getProperty("line.separator"));
        sb.append("}");

        if(hasHandlers==false)
            return null;

        return sb.toString();
    }

    public static String transformToVerily(VerilyTable routers, VerilyTable methods, Path p, String rel) throws IOException {

        List<String> lineBuffer = new ArrayList<String>();


        String context = p.getFileName().toString().replace(".java", "");

        InputStream is = new FileInputStream(p.toFile());

        List<String> lastOnFailClause = new ArrayList<String>();
        int lastFailClausePosition = -1;

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        int i=0;
        while ( (line = br.readLine()) != null) {

            i++;

            // see if it's a method declaration.
            {

                if(line.contains("Utils.assertionFailureL")){

                    int start = line.indexOf("Utils.assertionFailureL");

                    sb.append(line.substring(0, start));
                    sb.append("throw new RuntimeException(\"_jmlValidationFail\");");

                }else {
                    sb.append(line);
                }

            sb.append(System.getProperty("line.separator"));
            }
        }

        is.close();

        return sb.toString();
    }

    public static String stripNonJML(VerilyTable routers, VerilyTable methods, Path p, String rel) throws IOException, JMLTransformationError {

        List<String> lineBuffer = new ArrayList<String>();


        String context = p.getFileName().toString().replace(".java", "");

        InputStream is = new FileInputStream(p.toFile());

        List<String> lastOnFailClause = new ArrayList<String>();
        int lastFailClausePosition = -1;

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        int i=0;
        while ( (line = br.readLine()) != null) {

            i++;

            if(line.trim().startsWith("//@")){
                List<String> tokens = tokens(line);

                // omit this line.
                if(tokens.get(0).equals("onFail")){
                    lastOnFailClause.addAll(tokens);
                    lastFailClausePosition = i;
                    continue;
                }
            }

            // see if it's a method declaration.
            {
                List<String> tokens = tokens(line);
                try {
                if(tokens.size() > 4 &&
                        tokens.get(0).equals("public") &&
                        tokens.get(1).equals("static") &&
                        tokens.get(2).equals("final")
                        )                             {

                    String method = tokens.get(4);


                    if(lastFailClausePosition!=-1){

                        // associate these two
                        //methods.methodAt(context, method).setOnFailClause();

                        String invocation = onFailTokensToClause(context, lastFailClausePosition, p, lastOnFailClause, routers, rel);

                        methods.methodAt(context, method.substring(0,method.indexOf('(')).trim()).setOnFailClause(invocation);

                        lastFailClausePosition = -1;
                        lastOnFailClause.clear();
                    }

                }



                }catch(MethodNotMappedException e){

                    if(lastFailClausePosition > 0){
                        throw new JMLTransformationError(String.format("[jml-transform] %s:%d - Found a onFail clause without a matching method", rel, lastFailClausePosition));
                    }


                }

            }

            sb.append(line);
            sb.append(System.getProperty("line.separator"));
            lineBuffer.add(line);
        }

        is.close();


        if(lastFailClausePosition > 0){
            throw new JMLTransformationError(String.format("[jml-transform] %s:%d - Found a onFail clause without a matching method",rel, lastFailClausePosition));
        }

        return sb.toString();
    }

    private static String onFailTokensToClause(String context, int line, Path file, List<String> tokens, VerilyTable routers, String rel) throws JMLTransformationError {

        if(tokens.size() < 3 || tokens.get(1).equals("<-")==false){
            throw new JMLTransformationError(String.format("[jml-transform] %s:%d - Invalid onFail method format", rel, line));
        }

        String method = tokens.get(2);
        int paren = method.indexOf('(');
        int semi  = method.indexOf(';');

        if(paren==-1){
            throw new JMLTransformationError(String.format("[jml-transform] %s:%d - Invalid onFail method format. Method call must be to an existing router", rel, line));
        }

        if(semi==-1){
            throw new JMLTransformationError(String.format("[jml-transform] %s:%d - Invalid onFail method format. Missing a ';'", rel, line));
        }


        String methodName = method.substring(0,paren).trim();

        try {
            VerilyMethod m = routers.methodAt(context, methodName);

            return method.substring(0,semi);

        }catch(MethodNotMappedException e){
            throw new JMLTransformationError(String.format("[jml-transform] %s:%d - Invalid onFail method format. Method call must be to an existing router", rel, line));
        }
    }
    private static List<String> tokens(String l){

        String [] parts = l.split(" ");

        List<String> ls = new ArrayList<String>();

        for(String p : parts){

            String pp = p.trim();

            if(pp.equals("") || pp.equals("//@"))
                continue;


            ls.add(pp);
        }

        return ls;
    }



}
