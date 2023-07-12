package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2023-07-11
 *
 * @author mgn
 */
public class Selenium4_vs_3_Tests extends AbstractWebDriverTest {

    /**
     * This test will pass if a Selenium 3 standalone server is used.
     * This test will fail (or go into a timeout) if a Selenium 4 standalone server is used.
     * Cause:
     * Selenium 4 standalone server knows nothing about any version of local browsers.
     */
    @Test
    public void testBrowserVersionAgainstSeleniumStandalone() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        request.setBrowserVersion("110");
        request.setBaseUrl("https://the-internet.herokuapp.com/");

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
    }

    @Test
    public void testBrowserCustomCaps_Selenium3() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        DesiredCapabilities desiredCapabilities = request.getDesiredCapabilities();
        desiredCapabilities.setCapability("foo", "bar");

        WEB_DRIVER_MANAGER.getWebDriver(request);
    }

//    @Test
//    public void testBrowserCustomCaps_Selenium4() {
//        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
//        MutableCapabilities caps = new MutableCapabilities();
//        caps.setCapability("foo", "bar");
//
//        request.getCapabilities()
//        request.getBrowserOptions().merge(caps);
//
//        WEB_DRIVER_MANAGER.getWebDriver(request);
//    }

}
