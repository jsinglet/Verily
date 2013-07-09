package content;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.log.Logger;
import freemarker.template.*;

import java.io.File;
import java.io.IOException;

public class TemplateFactory {

    private static TemplateFactory templateFactory;
    private Configuration templateConfig;

    private TemplateFactory() {
    }

    public static TemplateFactory getInstance() throws IOException {
        if (templateFactory == null) {

            try {
                Logger.selectLoggerLibrary(Logger.LIBRARY_SLF4J);
            } catch (ClassNotFoundException e) {
                // bad, but we can march on
                e.printStackTrace();
            }

            templateFactory = new TemplateFactory();
            templateFactory.templateConfig = new Configuration();


            // TODO - test this first path
            FileTemplateLoader ftl = new FileTemplateLoader(new File("src/main/resources/"));
            ClassTemplateLoader ctl = new ClassTemplateLoader(templateFactory.getClass(), "/");
            TemplateLoader[] loaders = new TemplateLoader[]{ftl, ctl};
            MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);

            templateFactory.templateConfig.setTemplateLoader(mtl);


            templateFactory.templateConfig.setObjectWrapper(new DefaultObjectWrapper());

            templateFactory.templateConfig.setDefaultEncoding("UTF-8");

            templateFactory.templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

            templateFactory.templateConfig.setIncompatibleImprovements(new Version(2, 3, 20));
        }

        return templateFactory;
    }


    public Template get404Template() throws IOException {
        Template t = templateConfig.getTemplate("404.ftl");
        return t;
    }

    public Template get400Template() throws IOException {
        Template t = templateConfig.getTemplate("400.ftl");
        return t;
    }

}

