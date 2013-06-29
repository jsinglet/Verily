import java.nio.file.Path;

public class PwEEnv {

    private Path home;
    private String appName;
    private String appVersion;


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
}
