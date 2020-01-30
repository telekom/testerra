package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.By;

/**
 * Interface for finding {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementFinder {
    UiElement find(Locate locator);
    default UiElement findById(Object id) {
        return find(Locate.by().id(id));
    }
    default UiElement findByQa(String qa) {
        return find(Locate.by().qa(qa));
    }
    default UiElement find(By by) {
        return find(Locate.by(by));
    }
    default UiElement findByXPath(XPath xPath) {
        return find(Locate.by(xPath));
    }
}
