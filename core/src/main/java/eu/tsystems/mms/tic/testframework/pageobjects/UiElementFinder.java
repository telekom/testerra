package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.By;

/**
 * Interface for finding {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementFinder {
    LocateBuilder Locator = new LocateBuilder();
    UiElement find(Locate locator);
    default UiElement findById(Object id) {
        return find(Locator.by(By.id(id.toString())));
    }
    default UiElement findByQa(String qa) {
        return find(Locator.byQa(qa));
    }
    default UiElement find(By by) {
        return find(Locator.by(by));
    }
    default UiElement findByXPath(XPath xPath) {
        return find(Locator.by(xPath));
    }
//    default UiElement findByCaption(String caption) {
//        Locate textLocator = Locate.by(XPath.from("*").text(caption)).displayed();
//
//        UiElement element = find(textLocator);
//
//        if (!element.present().getActual()) {
//            Locate titleLocator = Locate.by(XPath.from("*").attribute(Attribute.TITLE, caption)).displayed();
//            element = find(titleLocator);
//
//            if (!element.present().getActual()) {
//                element = findInFrames(textLocator);
//
//                if (!element.present().getActual()) {
//                    element = findInFrames(titleLocator);
//                }
//            }
//        }
//        return element;
//    }
    default UiElement findInFrames(Locate locator) {
        return null;
    }
}
