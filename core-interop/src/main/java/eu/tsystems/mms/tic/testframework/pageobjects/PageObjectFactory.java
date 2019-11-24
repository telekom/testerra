package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import org.openqa.selenium.WebDriver;

public interface PageObjectFactory {
    PageObjectFactory setGlobalPagePrefix(String pagePrefix);
    PageObjectFactory setThreadLocalPagePrefix(String pagePrefix);
    PageObjectFactory removeThreadLocalPagePrefix();

    /**
     * @todo Make default when WebDriverManager has an interface
     */
    <T extends PageObject> T createPage(Class<T> pageClass);
    default <T extends PageObject> T createPage(Class<T> pageClass, WebDriver webDriver) {
        return createPageWithCheckRule(pageClass, webDriver, CheckRule.IS_DISPLAYED);
    }
    <T extends PageObject> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver);
    <T extends IComponent> T createComponent(Class<T> componentClass, PageObject page, IGuiElement rootElement);
    /**
     * @todo Make default when WebDriverManager has an interface
     */
    <T extends PageObject> T createPageWithCheckRule(Class<T> pageClass, CheckRule checkRule);
    <T extends PageObject> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule);

}
