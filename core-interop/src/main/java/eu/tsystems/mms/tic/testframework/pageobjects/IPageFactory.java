package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import org.openqa.selenium.WebDriver;

public interface IPageFactory {
    IPageFactory setGlobalPagePrefix(String pagePrefix);
    IPageFactory setThreadLocalPagePrefix(String pagePrefix);
    IPageFactory removeThreadLocalPagePrefix();

    /**
     * @todo Make default when WebDriverManager has an interface
     */
    <T extends IPage> T createPage(Class<T> pageClass);
    default <T extends IPage> T createPage(Class<T> pageClass, WebDriver webDriver) {
        return createPageWithCheckRule(pageClass, webDriver, CheckRule.IS_DISPLAYED);
    }
    <T extends IPage> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver);
    <T extends IComponent> T createComponent(Class<T> componentClass, IPage page, IGuiElement rootElement);
    /**
     * @todo Make default when WebDriverManager has an interface
     */
    <T extends IPage> T createPageWithCheckRule(Class<T> pageClass, CheckRule checkRule);
    <T extends IPage> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule);

}
