package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.WebDriver;

/**
 * This Factory should return instances of Page
 * But there exists currently no interface for it.
 * @todo Create interface for Page
 */
public interface IPageFactory {
    <T extends WebDriverRetainer> T create(Class<T> pageClass);
    <T extends WebDriverRetainer> T create(Class<T> pageClass, WebDriver driver);
}
