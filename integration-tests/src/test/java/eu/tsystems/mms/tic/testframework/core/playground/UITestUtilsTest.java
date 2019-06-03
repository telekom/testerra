package eu.tsystems.mms.tic.testframework.core.playground;

import eu.tsystems.mms.tic.testframework.testing.FennecTest;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

public class UITestUtilsTest extends FennecTest {

    static {
        DesiredCapabilities caps = new DesiredCapabilities();
        WebDriverManagerUtils.addProxyToCapabilities(caps, "proxy.mms-dresden.de:8080");
        WebDriverManager.setGlobalExtraCapabilities(caps);
    }

    @Test
    public void testT01_takeScreenshot() {
        DesktopWebDriverRequest r = new DesktopWebDriverRequest();
        r.baseUrl = "http://www.google.de";

        r.browser = "firefox";
        r.browserVersion = "61";

        r.webDriverMode = WebDriverMode.remote;
        r.seleniumServerHost = "localhost";
        r.seleniumServerPort = "4444";
        WebDriver driver = WebDriverManager.getWebDriver(r);
        UITestUtils.takeScreenshot(driver, true);
    }

}
