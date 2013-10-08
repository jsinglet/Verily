package verily.lang;

public class TextContent extends Content {
    public TextContent(String content) {
        super(content);
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }
}
