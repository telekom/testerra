package eu.tsystems.mms.tic.testframework.report.model;

public interface BrowserInformation {

    BrowserInformation parseUserAgent(String userAgent);

    /**
     * Gets the browser name of the test run.
     *
     * @return the browser name.
     */
    String getBrowserName();

    /**
     * Gets the browser version of the test run.
     *
     * @return the browser version.
     */
    String getBrowserVersion();

    @Deprecated
    String getUserAgent();
}
