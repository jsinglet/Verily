import pwe.lang.PwETable;
import pwe.lang.exceptions.MethodNotMappedException;

import java.nio.file.Path;

public class PwEEnv {

    private Path home;
    private String appName;
    private String appVersion;
    private PwETable translationTable;
    private int port;


    public Path getHome() {
        return home;
    }

    public void setHome(Path home) {
        this.home = home;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public PwETable getTranslationTable() {
        return translationTable;
    }

    public void setTranslationTable(PwETable translationTable) {
        this.translationTable = translationTable;
    }

    public void findMappedMethod(String context, String method) throws MethodNotMappedException {
        translationTable.methodAt(context, method);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}