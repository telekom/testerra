package eu.tsystems.mms.tic.testframework.report.model;

import java.util.List;

public interface BrowserInformation {

    BrowserInformation parseUserAgent(String userAgent);

//    /**
//     * Introduce a browserInfo Text with a steps string.
//     *
//     * @param browserInfo .
//     * @param context .
//     */
//    void setBrowserInfoWithContext(String browserInfo, String context);
//
//    /**
//     * gives the steps List of a given browserinfo key
//     *
//     * @param key .
//     * @return steps List of the given browser
//     */
//    List<String> getContext(String key);
//
//    /**
//     * Checks whether there are more then one key in the Map
//     * , so there are different browser versions in the key list
//     *
//     * @return true if there are different browsers in the Map
//     */
//    boolean detectDifferentBrowsers();
//
//    /**
//     * checks whether there is any browser in the map
//     *
//     * @return true if there is no browser in the map
//     */
//    boolean detectAnyBrowser();

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
