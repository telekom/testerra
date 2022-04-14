package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * Created on 14.04.2022
 *
 * @author mgn
 */
public class OldWebDriverTest extends TesterraTest implements Loggable {

    @Test
    public void T01WebDriverManager() {
        WebDriver driver = WebDriverManager.getWebDriver();
    }

}
