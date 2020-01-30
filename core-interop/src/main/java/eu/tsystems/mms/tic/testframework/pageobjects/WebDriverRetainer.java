package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.WebDriver;

/**
 * Interface for classes retaining an {@link WebDriver} instance
 * @author Mike Reiche
 */
public interface WebDriverRetainer {
    WebDriver getWebDriver();
}
