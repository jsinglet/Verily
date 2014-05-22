package content;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.log.Logger;
import freemarker.template.*;
import org.slf4j.LoggerFactory;
import verily.lang.VerilyParserModes;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateFactory {

    private static TemplateFactory templateFactory;
    private Configuration templateConfig;
    private Path temp;

    org.slf4j.Logger logger = LoggerFactory.getLogger(TemplateFactory.class);


    private TemplateFactory() {
    }

    public static TemplateFactory getInstance() throws IOException {
        if (templateFactory == null) {

            org.slf4j.Logger logger = LoggerFactory.getLogger(TemplateFactory.class);


//            try {
//                Logger.selectLoggerLibrary(Logger.LIBRARY_SLF4J);
//            } catch (ClassNotFoundException e) {
//                // bad, but we can march on
//                e.printStackTrace();
//            }

            templateFactory = new TemplateFactory();
            templateFactory.templateConfig = new Configuration();

            // built in layouts
            logger.info("Loading internal layouts...");
            templateFactory.templateConfig.addAutoImport("verilyLayouts", "verily/layouts/layouts.ftl");
            templateFactory.templateConfig.addAutoImport("verilyUtils", "verily/layouts/app.ftl");


            logger.info("Internal layouts loaded @ root verilyLayouts");


            // TODO - test this first path
            templateFactory.temp = Files.createTempDirectory("._Verily");
            FileTemplateLoader ftl1 = new FileTemplateLoader(templateFactory.temp.toFile());

            FileTemplateLoader ftl = new FileTemplateLoader(new File("src/main/resources/"));
            ClassTemplateLoader ctl = new ClassTemplateLoader(templateFactory.getClass(), "/");
            TemplateLoader[] loaders = new TemplateLoader[]{ftl1, ftl, ctl};
            MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);

            templateFactory.templateConfig.setTemplateLoader(mtl);


            templateFactory.templateConfig.setObjectWrapper(new DefaultObjectWrapper());

            templateFactory.templateConfig.setDefaultEncoding("UTF-8");

            templateFactory.templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

            //templateFactory.templateConfig.setIncompatibleImprovements(new Version(2, 3, 20));

            templateFactory.reloadUserTemplates();


        }

        return templateFactory;
    }

    public void reloadUserTemplates() {

        templateConfig.removeAutoImport("verily");

        //
        //user layouts
        //
        // this involves a little bit of magic so we need to generate some code here..
        try {

            logger.info("Loading user templates...");

            Map<String, String> userTemplates = new HashMap<String, String>();

            Path base = Paths.get("");

            DirectoryStream<Path> userTemplateFiles = Files.newDirectoryStream(base.resolve("src").resolve("main").resolve("resources").resolve("layouts"), "*.ftl");

            for (Path p : userTemplateFiles) {


                String templateFileName = p.getFileName().toString();
                String templateName = p.getFileName().toString().split("\\.")[0];

                logger.info("Importing user layout: {}", templateName);


                userTemplates.put(templateName, new String(Files.readAllBytes(p)));

            }

            List<Map<String,String>> templateModel = new ArrayList<Map<String,String>>();

            for(String templateName : userTemplates.keySet()){

                Map<String,String> m = new HashMap<String,String>();

                m.put("name", templateName);
                m.put("content", userTemplates.get(templateName));

                templateModel.add(m);


            }

            // create the data model
            Map model = new HashMap();

            model.put("templates", templateModel);


            // get the temp file
            Path tempFile = Files.createTempFile(temp, "verilyUserTemplates", ".ftl");

            Writer body = new FileWriter(tempFile.toFile());

            // generate the code
            Template t = getLayoutTemplate();
            t.process(model, body);

            body.close();

            logger.info("Combined templates @ {}", tempFile.toAbsolutePath().toString());
            // add it as an auto import.
            templateFactory.templateConfig.addAutoImport("verily", tempFile.getFileName().toString());


        } catch (Exception e) {
            logger.info("Didn't load user templates: {}", e.getMessage());
        }

    }

    public static TemplateFactory getBootstrapInstance() throws IOException {
        if (templateFactory == null) {

            try {
                Logger.selectLoggerLibrary(Logger.LIBRARY_SLF4J);
            } catch (ClassNotFoundException e) {
                // bad, but we can march on
                e.printStackTrace();
            }

            templateFactory = new TemplateFactory();
            templateFactory.templateConfig = new Configuration();


            ClassTemplateLoader ctl = new ClassTemplateLoader(templateFactory.getClass(), "/");
            TemplateLoader[] loaders = new TemplateLoader[]{ctl};
            MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);

            templateFactory.templateConfig.setTemplateLoader(mtl);


            templateFactory.templateConfig.setObjectWrapper(new DefaultObjectWrapper());

            templateFactory.templateConfig.setDefaultEncoding("UTF-8");

            templateFactory.templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

            templateFactory.templateConfig.setIncompatibleImprovements(new Version(2, 3, 20));
        }

        return templateFactory;
    }


    public Template getUserTemplate(String template) throws IOException {
        Template t = templateConfig.getTemplate(template);
        return t;
    }

    public Template get404Template() throws IOException {
        Template t = templateConfig.getTemplate("404.ftl");
        return t;
    }

    public Template getAjaxTemplate() throws IOException {
        Template t = templateConfig.getTemplate("verily/app.ftl");
        return t;
    }

    public Template get400Template() throws IOException {
        Template t = templateConfig.getTemplate("400.ftl");
        return t;
    }

    public Template get500Template() throws IOException {
        Template t = templateConfig.getTemplate("500.ftl");
        return t;
    }

    public Template getMethodTemplate() throws IOException {
        Template t = templateConfig.getTemplate("classes/Method.ftl");
        return t;
    }


    public Template getRouterTemplate() throws IOException {
        Template t = templateConfig.getTemplate("classes/Router.ftl");
        return t;
    }

    public Template getTestTemplate() throws IOException {
        Template t = templateConfig.getTemplate("classes/Test.ftl");
        return t;
    }


    public Template getPOMTemplate() throws IOException {
        Template t = templateConfig.getTemplate("classes/POM.ftl");
        return t;
    }


    public Template getLayoutTemplate() throws IOException {
        Template t = templateConfig.getTemplate("verily/layouts/verily.ftl");
        return t;
    }


}

