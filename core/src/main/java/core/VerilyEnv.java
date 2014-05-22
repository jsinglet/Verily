package core;

import verily.lang.VerilyMethod;
import verily.lang.VerilyTable;
import verily.lang.exceptions.MethodNotMappedException;
import verily.lang.util.MRRTableSet;

import java.nio.file.Path;

public class VerilyEnv {

    private Path home;
    private int numberOfThreads;
    private String appName;
    private String appVersion;
    private MRRTableSet translationTable;
    private int port;
    private boolean reload;
    private boolean daemon;
    private boolean enableContracts;
    private String jmlHome;
    private String z3Home;
    private boolean noEsc;


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

    public MRRTableSet getTranslationTable() {
        return translationTable;
    }

    public void setTranslationTable(MRRTableSet translationTable) {
        this.translationTable = translationTable;
    }

    public VerilyMethod findMappedMethod(String context, String method) throws MethodNotMappedException {
        return translationTable.getMethodTable().methodAt(context, method);
    }

    public VerilyMethod findMappedRouter(String context, String method) throws MethodNotMappedException {
        return translationTable.getRouterTable().methodAt(context, method);
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public synchronized boolean isReload() {
        return reload;
    }

    public synchronized void setReload(boolean reload) {
        this.reload = reload;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public boolean isEnableContracts() {
        return enableContracts;
    }

    public void setEnableContracts(boolean enableContracts) {
        this.enableContracts = enableContracts;
    }

    public String getJmlHome() {
        return jmlHome;
    }

    public void setJmlHome(String jmlHome) {
        this.jmlHome = jmlHome;
    }

    public String getZ3Home() {
        return z3Home;
    }

    public void setZ3Home(String z3Home) {
        this.z3Home = z3Home;
    }

    public boolean isNoEsc() {
        return noEsc;
    }

    public void setNoEsc(boolean noEsc) {
        this.noEsc = noEsc;
    }
}