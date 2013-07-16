package pwe.lang;

public class HTMLContent extends Content {

    public HTMLContent(String content) {
        super(content);
    }

    public HTMLContent() {
    }

    @Override
    public String getContentType() {
        return "text/html";
    }
}
