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
    default UiElement find(XPath xPath) {
        return find(Locator.by(xPath));
    }
    default UiElement findByCaption(String caption) {
        Locate textLocator = Locator.by(XPath.from("*").text().is(caption)).displayed();

        UiElement element = find(textLocator);

        if (!element.present().getActual()) {
            Locate titleLocator = Locator.by(XPath.from("*").attribute(Attribute.TITLE).is(caption)).displayed();
            element = find(titleLocator);

            if (!element.present().getActual()) {
                Locate valueLocator = Locator.by(XPath.from("*").attribute(Attribute.VALUE).is(caption)).displayed();
                element = find(valueLocator);
            }

//            if (!element.present().getActual()) {
//                element = findInFrames(textLocator);
//
//                if (!element.present().getActual()) {
//                    element = findInFrames(titleLocator);
//                }
//            }
        }
        return element;
    }
//    default UiElement findInFrames(Locate locator) {
//        return null;
//    }
}
