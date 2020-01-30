package eu.tsystems.mms.tic.testframework.testing;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.WebDriverRetainer;
import org.openqa.selenium.WebDriver;

/**
 * Interface for creating {@link UiElement} by given {@link WebDriver}
 * @author Mike Reiche
 */
public interface UiElementCreator extends
    UiElementFinder,
    WebDriverRetainer
{
    UiElementFactory uiElementFactory = Testerra.injector.getInstance(UiElementFactory.class);

    @Override
    default UiElement find(Locate locator) {
        return uiElementFactory.createWithWebDriver(getWebDriver(), locator);
    }
}
