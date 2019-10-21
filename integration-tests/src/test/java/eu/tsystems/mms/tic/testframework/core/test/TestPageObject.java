package eu.tsystems.mms.tic.testframework.core.test;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.WebDriver;

public class TestPageObject extends Page {
    /**
     * Constructor for existing sessions.
     *
     * @param driver .
     */
    public TestPageObject(WebDriver driver) {
        super(driver);
    }
}
