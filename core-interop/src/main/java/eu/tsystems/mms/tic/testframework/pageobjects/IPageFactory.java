package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.WebDriver;

public interface IPageFactory {
    IPageFactory setGlobalPagePrefix(String pagePrefix);
    IPageFactory setThreadLocalPagePrefix(String pagePrefix);
    IPageFactory removeThreadLocalPagePrefix();
    <T extends IPage> T createPage(Class<T> pageClass);
    <T extends IPage> T createPage(Class<T> pageClass, WebDriver webDriver);
    <T extends IPage> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver);
    <T extends IComponent> T createComponent(Class<T> componentClass, IPage page, IGuiElement rootElement);
}
