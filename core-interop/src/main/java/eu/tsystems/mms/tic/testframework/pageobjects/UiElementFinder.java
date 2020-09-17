package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.By;

/**
 * Interface for finding {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementFinder extends UiElementLocator {
    UiElement find(Locate locator);
    default UiElement findById(Object id) {
        return find(Locate.by(By.id(id.toString())));
    }
    default UiElement findByQa(String qa) {
        return find(Locate.byQa(qa));
    }
    default UiElement find(By by) {
        return find(Locate.by(by));
    }
    default UiElement find(XPath xPath) {
        return find(Locate.by(xPath));
    }
}
