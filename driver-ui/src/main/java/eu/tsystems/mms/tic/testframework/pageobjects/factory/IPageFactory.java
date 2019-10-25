package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.WebDriver;

public interface IPageFactory {
    <T extends Page> T create(Class<T> pageClass);
    <T extends Page> T create(Class<T> pageClass, WebDriver driver);
}
