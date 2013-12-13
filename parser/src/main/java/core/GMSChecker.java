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

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

/**
 * @author John L. Singleton <jsinglet@gmail.com>
 */
public class GMSChecker {

    final Logger logger = LoggerFactory.getLogger(GMSChecker.class);
    private List<GMSAccess> accessList = new LinkedList<GMSAccess>();

    private Path base;

    public static void main(String args[]) throws IOException, InterruptedException {
        // check all the files in the current directory.

        DirectoryStream<Path> controllerFiles = Files.newDirectoryStream(Paths.get(""));
        for (Path p : controllerFiles) {
            if(p.toFile().isDirectory()){
                GMSChecker h = new GMSChecker(p);

                h.check();
                h.graph1();
                h.graph2();
                h.graph3();
                h.graph4();

                //h.csv1();
            }
        }
    }

    public GMSChecker(Path base) {
        this.base = base;
    }


    public void csv1() throws IOException {

        base.resolve("_verily").toFile().mkdir();

        File out = base.resolve("_verily").resolve("gms-data.csv").toFile();
        FileOutputStream fos = new FileOutputStream(out);
        OutputStreamWriter os = new OutputStreamWriter(fos);

        os.write("Variable,Writes,Reads,Network Size,GMC,GMI\n");

        List<String> vars = GMSAccess.vars(accessList);

        for(String v : vars){

            int read  = 0;
            int write = 0;
            int n     = 0;

            for(GMSAccess node : accessList){

                if(node.getName().equals(v)==false)
                    continue;

                if(node.getType()== GMSAccess.AccessType.W){
                    write++;
                }else{
                    read++;
                }
                n++;
            }

            int indegree = write*read;


            double gmc = (double)write * ((double)indegree/(double)read);
            double gmi = gmc*(read+write)/(double)accessList.size();

            // write, read, n, gms, network impact
            if(read>0)
                os.write(String.format("%s,%d,%d,%d,%f,%f\n", v, write, read, n, gmc, gmi));




        }




        os.close();

    }

    // outputs graph to dot format
    public void graph4() throws IOException, InterruptedException {

        base.resolve("_verily").toFile().mkdir();

        File out = base.resolve("_verily").resolve("graph4.gv").toFile();
        FileOutputStream fos = new FileOutputStream(out);
        OutputStreamWriter os = new OutputStreamWriter(fos);

        // file header
        os.write("digraph G {\n");
        os.write("\tsize=\"8.5,11\"; ratio=\"fill\"; node[fontsize=24];\n");

        List<GMSAccess> writes = GMSAccess.writes(accessList);
        List<GMSAccess> reads = GMSAccess.reads(accessList);
        List<String> modules = GMSAccess.modules(accessList);

        // header
        for(GMSAccess write : writes){

            List<GMSAccess> l = GMSAccess.sublistByName(write.getName(), reads);

            if(l.size()>0)
                os.write(String.format("\t\"%s.%s:%d\"[shape=point];\n", pathToModule(write.getModule()), write.getName(), write.getLine()));

            for(GMSAccess node : l){
                os.write(String.format("\t\"%s.%s:%d\"[shape=point];\n", pathToModule(node.getModule()), node.getName(), node.getLine()));
            }
        }



        for(GMSAccess write : writes){

            List<GMSAccess> l = GMSAccess.sublistByName(write.getName(), reads);

            for(GMSAccess read : l){
                // arrow from METHOD use to the write.


                os.write(
                        String.format("\t\"%s.%s:%d\" -> \"%s.%s:%d\"\n",
                                pathToModule(read.getModule()),
                                read.getName(),
                                read.getLine(),
                                pathToModule(write.getModule()),
                                write.getName(),
                                write.getLine()));
            }
        }

        os.write("labelloc=\"t\"\n");
        os.write("label=\"GMS Dependency Graph (n > 0)\"\n");
        os.write("}\n");

        /// display the file
        os.close();

        File ps = base.resolve("_verily").resolve(base.toFile().getName()+"-graph4.ps").toFile();


        Runtime r = Runtime.getRuntime();
        Process p1 = r.exec(String.format("C:/Program Files (x86)/Graphviz2.34/bin/sfdp -Tps %s -o %s", out.getPath(), ps.getPath()));
        p1.waitFor();


        Desktop.getDesktop().open(ps);

    }


    // outputs graph to dot format
    public void graph3() throws IOException, InterruptedException {

        //File out = File.createTempFile("gmsgraph", ".gv");
        base.resolve("_verily").toFile().mkdir();
        File out = base.resolve("_verily").resolve("graph3.gv").toFile();
        FileOutputStream fos = new FileOutputStream(out);
        OutputStreamWriter os = new OutputStreamWriter(fos);

        // file header
        os.write("digraph G {\n");
        os.write("\tsize=\"8.5,11\"; ratio=\"fill\"; node[fontsize=24];\n");

        List<GMSAccess> writes = GMSAccess.writes(accessList);
        List<GMSAccess> reads = GMSAccess.reads(accessList);
        List<String> modules = GMSAccess.modules(accessList);

        // header
        for(GMSAccess node : accessList){
            os.write(String.format("\t\"%s.%s:%d\"[shape=point];\n", pathToModule(node.getModule()), node.getName(), node.getLine()));
        }



        for(GMSAccess write : writes){

            List<GMSAccess> l = GMSAccess.sublistByName(write.getName(), reads);

            for(GMSAccess read : l){
                // arrow from METHOD use to the write.


                os.write(
                        String.format("\t\"%s.%s:%d\" -> \"%s.%s:%d\"\n",
                                pathToModule(read.getModule()),
                                read.getName(),
                                read.getLine(),
                                pathToModule(write.getModule()),
                                write.getName(),
                                write.getLine()));
            }
        }

        os.write("labelloc=\"t\"\n");
        os.write("label=\"GMS Dependency Graph (n >= 0)\"\n");

        os.write("}\n");

        /// display the file
        os.close();

        File ps = base.resolve("_verily").resolve("graph3.ps").toFile();

        Runtime r = Runtime.getRuntime();
        Process p1 = r.exec(String.format("C:/Program Files (x86)/Graphviz2.34/bin/sfdp -Tps %s -o %s", out.getPath(), ps.getPath()));
        p1.waitFor();


       Desktop.getDesktop().open(ps);

    }

    // outputs graph to dot format
    public void graph2() throws IOException, InterruptedException {

        //File out = File.createTempFile("gmsgraph", ".gv");
        base.resolve("_verily").toFile().mkdir();
        File out = base.resolve("_verily").resolve("graph2.gv").toFile();
        FileOutputStream fos = new FileOutputStream(out);
        OutputStreamWriter os = new OutputStreamWriter(fos);

        // file header
        os.write("digraph G {\n");
        os.write("\tsize=\"8.5,11\"; ratio=\"fill\"; node[fontsize=24];\n");

        List<GMSAccess> writes = GMSAccess.writes(accessList);
        List<GMSAccess> reads = GMSAccess.reads(accessList);
        List<String> modules = GMSAccess.modules(accessList);


        for(GMSAccess write : writes){

            List<GMSAccess> l = GMSAccess.sublistByName(write.getName(), reads);

            for(GMSAccess read : l){
                // arrow from METHOD use to the write.


                os.write(
                        String.format("\t\"%s.%s:%d\" -> \"%s.%s:%d\"\n",
                                pathToModule(read.getModule()),
                                read.getName(),
                                read.getLine(),
                                pathToModule(write.getModule()),
                                        write.getName(),
                                        write.getLine()));
            }
        }

        os.write("labelloc=\"t\"\n");
        os.write("label=\"GMS Dependency Graph (Module Detail)\"\n");

        os.write("}\n");

        /// display the file
        os.close();

        File ps = base.resolve("_verily").resolve("graph2.ps").toFile();

        Runtime r = Runtime.getRuntime();
        Process p1 = r.exec(String.format("C:/Program Files (x86)/Graphviz2.34/bin/sfdp -Tps %s -o %s", out.getPath(), ps.getPath()));
        p1.waitFor();


        Desktop.getDesktop().open(ps);

    }

        // outputs graph to dot format
    public void graph1() throws IOException, InterruptedException {

        //File out = File.createTempFile("gmsgraph", ".gv");
        base.resolve("_verily").toFile().mkdir();
        File out = base.resolve("_verily").resolve("graph1.gv").toFile();




        FileOutputStream fos = new FileOutputStream(out);
        OutputStreamWriter os = new OutputStreamWriter(fos);

        // file header
        os.write("digraph G {\n");
        os.write("\tsize=\"8.5,11\"; ratio=\"fill\"; node[fontsize=24];\n");

        List<GMSAccess> writes = GMSAccess.writes(accessList);
        List<GMSAccess> reads = GMSAccess.reads(accessList);
        List<String> modules = GMSAccess.modules(accessList);

        // first define all methods and variables
        for(String module : modules){

            List<String> methods = GMSAccess.methodsInModule(reads, module);
            List<String> vars    = GMSAccess.varsInModule(writes, module);

            for(String method : methods){
                os.write(String.format("\t\"method_%s.%s\"[label=\"%s\", shape=point];\n", pathToModule(module), method, String.format("%s()", method)));
            }

            for(String var : vars){
                os.write(String.format("\t\"var_%s.%s\"[label=\"%s\", style=invis, shape=point, width=0, height=0];\n", pathToModule(module), var, var));
            }

            os.write(String.format("\t\"var_%s.self\"[label=\"self\", shape=point];\n", pathToModule(module)));


        }

        // find all things that depend on these
        for(GMSAccess write : writes){

            List<GMSAccess> l = GMSAccess.sublistByName(write.getName(), reads);

            for(GMSAccess read : l){
                // arrow from METHOD use to the write.

                // if this is an inter module access, just point at self
                if(read.getModule().equals(write.getModule())){

                    os.write(
                            String.format("\t\"method_%s.%s\" -> \"var_%s.self\";\n",
                                    pathToModule(read.getModule()),
                                    read.getMethod(),
                                    pathToModule(write.getModule())
                                    ));

                }else{
               os.write(
                       String.format("\t\"method_%s.%s\" -> \"var_%s.%s\" [lhead=\"cluster%s\"];\n",
                               pathToModule(read.getModule()),
                               read.getMethod(),
                               pathToModule(write.getModule()),
                               write.getName(),
                               write.getModule()));
                }
            }
        }

        // create the clusters

        //subgraph "fol.java" {label = "fool.java", thing1; thing2;}
        for(String module : modules){

            List<String> methods = GMSAccess.methodsInModule(reads, module);
            List<String> vars    = GMSAccess.varsInModule(writes, module);

            if(methods.size()==0 && vars.size()==0)
                continue;

            os.write(String.format("\tsubgraph \"cluster%s\" {label=\"%s\"; ", module, pathToModule(module)));



            for(String method : methods){
                os.write(String.format("\"method_%s.%s\";", pathToModule(module), method));
            }

            for(String var : vars){
                os.write(String.format("\"var_%s.%s\";", pathToModule(module), var));
            }

            os.write(String.format("\t\"var_%s.self\";\n", pathToModule(module)));

            os.write("labelloc=\"t\"\n");
            os.write("label=\"GMS Dependency Graph (w/Intra-Module)\"\n");


            os.write("}\n");

        }


        // next, make a link from "main" to every module.
        for(String module : modules){
        //    os.write(String.format("\tApplicationMain -> %s;\n", pathToModule(module)));
        }


        // next, make a link from "main" to every module.
        for(String module : modules){
            List<String> vars = GMSAccess.varsInModule(accessList, module);

            for(String var : vars){
          //      os.write(String.format("\t%s -> %s;\n", pathToModule(module), var));
            }
        }




        // close file
        os.write("}\n");

        /**
         * digraph G {
         main -> parse -> execute;
         main -> init;
         main -> cleanup;
         execute -> make_string;
         execute -> printf;
         init -> make_string;
         main -> printf;
         execute -> compare;
         }
         */




        os.close();

        File ps = base.resolve("_verily").resolve("graph1.ps").toFile();

        Runtime r = Runtime.getRuntime();
        Process p1 = r.exec(String.format("C:/Program Files (x86)/Graphviz2.34/bin/dot -Gcompound=true -Tps %s -o %s", out.getPath(), ps.getPath()));
        p1.waitFor();


        Desktop.getDesktop().open(ps);


        //
        //dot -Gcompound=true -Tps gms.gv -o test2.ps


    }

    public static String pathToModule(String path){
        String [] splits = path.split("\\\\");
        return splits[splits.length-1].replaceAll("\\.java", "");
    }

    public int limit = 3;
    public void checkJava() throws IOException {



        Files.walkFileTree(base, new SimpleFileVisitor<Path>(){


            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                if(file.toString().endsWith(".java") && limit > 0){
                    List<GMSAccess> a = parseFile(file.toString());

                    if(a!=null){
                        accessList.addAll(a);
                       // limit--;
                    }


                }

                return super.visitFile(file, attrs);
            }
        });

    }

    public void checkJSPs(){

    }



    public void check() throws IOException {
        checkJava();
        checkJSPs();
    }



    public List<GMSAccess> parseFile(String f) {
        boolean gui = true;
        boolean printTree = true;

        try {
            logger.info("Parsing file: {}", f);

            Lexer lexer = new JavaLexer(new ANTLRFileStream(f));

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            long start = System.currentTimeMillis();
            tokens.fill();
            long stop = System.currentTimeMillis();
            logger.info(String.format("File [%s] (lexed in %d ms)", f, stop - start));

            JavaParser parser = new JavaParser(tokens);
            ParserRuleContext t = parser.compilationUnit();

            //if (gui) t.inspect(parser);
//            if (printTree) System.out.println(t.toStringTree(parser));

            ParseTreeWalker walker = new ParseTreeWalker();
            GMSClassInfoListener extractor = new GMSClassInfoListener(parser, f);
            walker.walk(extractor, t);

            return extractor.getAccessList();

        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();
        }

        return null;
    }




}
