package eu.tsystems.mms.tic.testframework.core.test.webdrivermanager;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for WebDriverManager
 * <p>
 * Date: 27.04.2020
 * Time: 06:49
 *
 * @author Eric Kubenka
 */
public class WebDriverManagerTest extends AbstractWebDriverTest {

    @Test
    public void testT01_isJavaScriptActivated() {

        final WebDriver webDriver = WebDriverManager.getWebDriver();
        final boolean javaScriptActivated = WebDriverManager.isJavaScriptActivated(webDriver);

        Assert.assertTrue(javaScriptActivated, "JavaScript activated by default.");
    }

    @Test
    public void testT02_introduceOwnDriver() {

        final String webDriverMode = PropertyManager.getProperty(TesterraProperties.WEBDRIVERMODE, "local");
        final String browser = PropertyManager.getProperty(TesterraProperties.BROWSER, Browsers.chromeHeadless);

        // Exit options for testcase
        if (!browser.equalsIgnoreCase(Browsers.chrome) && !browser.equalsIgnoreCase(Browsers.chromeHeadless)) {
            throw new RuntimeException("This testcase only runs for chrome or chrome headless");
        }

        if (!webDriverMode.equalsIgnoreCase("local")) {
            throw new RuntimeException("This testcase only runs for local executions.");
        }

        // create new options
        final ChromeOptions chromeOptions = new ChromeOptions();

        if (browser.equalsIgnoreCase(Browsers.chromeHeadless)) {
            chromeOptions.setHeadless(true);
        }

        // create driver - window is open now
        final WebDriver driver = new ChromeDriver(chromeOptions);

        // assert that webdrivermanager does not know about this session.
        Assert.assertFalse(WebDriverManager.hasSessionsActiveInThisThread(), "WebDriver Session active in this thread.");

        // introduce it.
        WebDriverManager.introduceDrivers(driver);

        // assert that webdrivermanager does know about the session.
        Assert.assertTrue(WebDriverManager.hasSessionsActiveInThisThread(), "WebDriver Session active in this thread.");
    }

}
