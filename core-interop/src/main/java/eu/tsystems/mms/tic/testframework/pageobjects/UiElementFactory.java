package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.WebDriver;

/**
 * Creates {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementFactory {
    UiElement createFromPage(PageObject page, Locate locator);
    UiElement createWithWebDriver(WebDriver webDriver, Locate locator);
    UiElement createFromParent(UiElement parent, Locate locator);
    UiElement createWithFrames(Locate locator, UiElement... frames);
}
