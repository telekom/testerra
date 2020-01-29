package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitTest {

    public WaitTest() {
        WebDriverWait wait = new WebDriverWait( WebDriverManager.getWebDriver(), 20);
        wait.until(ExpectedConditions.urlContains("lightning/"));
    }
}
