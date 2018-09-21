package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.BrowserDataProvider;
import eu.tsystems.mms.tic.testframework.report.model.TestReportSevenWebDriverSetupConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by fakr on 17.10.2017
 */
public class WebDriverSetupTest extends AbstractTest {

    private static final String SELENIUM_GRID_HOST = PropertyManager.getProperty("selenium.server.host");
    private static final String SELENIUM_GRID_PORT = PropertyManager.getProperty("selenium.server.port");


    /***
     * Closes all current sessions so that the next webbrowser-session can be initialized
     */
    @BeforeMethod
    public void closeSessions () {
        WebDriverManager.shutdown();
    }

    @Test(dataProvider = "configDP", dataProviderClass = BrowserDataProvider.class)
    public void testConfigurations(TestReportSevenWebDriverSetupConfig wdsConfig) {

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.browser = wdsConfig.getBrowser();
        request.seleniumServerHost = SELENIUM_GRID_HOST;
        request.seleniumServerPort = SELENIUM_GRID_PORT;
        request.storedSessionId = wdsConfig.name();
        request.webDriverMode = WebDriverMode.remote;
        request.browserVersion = wdsConfig.getVersion();

        String proxyString = String.format("%s:%s", PropertyManager.getProperty("https.proxyHost"), PropertyManager.getProperty("https.proxyPort"));
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyString).setSslProxy(proxyString);
        WebDriverManager.setGlobalExtraCapability(CapabilityType.PROXY, proxy);
        WebDriver webDriver = WebDriverManager.getWebDriver(request);

        Assert.assertTrue(webDriver.getTitle() != null, "The web browser seems NOT to be open for " + wdsConfig.getBrowser() + "v" + wdsConfig.getVersion());
    }

}
