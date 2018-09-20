package eu.tsystems.mms.tic.testframework.report.model;


/**
 * Created by fakr on 17.10.2017
 */
public enum TestReportSevenWebDriverSetupConfig {

    FIREFOXv57("firefox", "57"),
    FIREFOXv58("firefox", "58"),
    CHROMEv62("chrome", "62"),
    CHROMEv63("chrome", "63"),
    CHROMEv64("chrome", "64");

    private String browser;
    private String version;

    TestReportSevenWebDriverSetupConfig(String browser, String version) {
        this.browser = browser;
        this.version = version;
    }

    public String getBrowser() {
        return this.browser;
    }

    public String getVersion() {
        return this.version;
    }
}
