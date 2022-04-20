package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created on 14.04.2022
 *
 * @author mgn
 */
public class OldWebDriverTest extends TesterraTest implements Loggable {

    @BeforeMethod
    public void before() {
        log().info("Before method");
        WebDriver driver = WebDriverManager.getWebDriver("default");
        driver.get("https://google.com");
        TimerUtils.sleep(5_000);
        WebDriverManager.getWebDriver("default");
    }

    @Test
    public void T01WebDriverManager() {
        WebDriver driver = WebDriverManager.getWebDriver("default");
        log().info("Test method finished");
    }

}
