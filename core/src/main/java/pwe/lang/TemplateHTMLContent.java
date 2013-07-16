package pwe.lang;

import content.TemplateFactory;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class TemplateHTMLContent extends HTMLContent {

    final Logger logger = LoggerFactory.getLogger(TemplateHTMLContent.class);


    private String template;
    private Map bindings;

    public TemplateHTMLContent(String template, Map bindings) {
        setBindings(bindings);
        setTemplate(template);
    }


    public TemplateHTMLContent(String content) {
        super(content);
    }

    public String getContent() {
        // construct the content from the given template
        Writer body = new StringWriter();

        Template t = null;

        try {
            t = TemplateFactory.getInstance().getUserTemplate(getTemplate());
            t.process(getBindings(), body);

            return body.toString();
        } catch (IOException e) {
            // can't find the template most likely...
            logger.info("Can't find template {}. Message: {}", getTemplate(), e.getMessage());
            setContentRenderErrorMessage("Unable to locate template " + getTemplate());
            setContentCode(404);

        } catch (TemplateException e) {
            logger.info("Error rendering template {}. Message: {}", getTemplate(), e.getMessage());
            setContentRenderErrorMessage("Fatal error rendering template. Please check logs.");
            setContentCode(500);

        }
        return null;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map getBindings() {
        return bindings;
    }

    public void setBindings(Map bindings) {
        this.bindings = bindings;
    }
}
