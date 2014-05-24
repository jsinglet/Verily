package core;

import java.io.File;

/**
 * Author: John L. Singleton <jsinglet@gmail.com>
 */
public class JMLTransformedSource {

    private File srcFile;
    private StringBuffer transformed;

    public JMLTransformedSource(File srcFile){
        this.srcFile = srcFile;
    }

    public File getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }

    public StringBuffer getTransformed() {
        return transformed;
    }

    public void setTransformed(StringBuffer transformed) {
        this.transformed = transformed;
    }
}
