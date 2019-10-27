package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.pageobjects.IWebDriverRetainer;
import org.openqa.selenium.WebDriver;

public interface IPageFactory {
    <T extends IWebDriverRetainer> T create(Class<T> pageClass);
    <T extends IWebDriverRetainer> T create(Class<T> pageClass, WebDriver driver);
}
