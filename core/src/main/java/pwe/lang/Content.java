package pwe.lang;

public abstract class Content {

    private String content;
    private int contentCode = 200;
    private String contentRenderErrorMessage;

    public abstract String getContentType();

    public Content(String content){
        setContent(content);
    }

    public Content(){
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public int getContentCode() {
        return contentCode;
    }

    public void setContentCode(int contentCode) {
        this.contentCode = contentCode;
    }

    public String getContentRenderErrorMessage() {
        return contentRenderErrorMessage;
    }

    public void setContentRenderErrorMessage(String contentRenderErrorMessage) {
        this.contentRenderErrorMessage = contentRenderErrorMessage;
    }
}
