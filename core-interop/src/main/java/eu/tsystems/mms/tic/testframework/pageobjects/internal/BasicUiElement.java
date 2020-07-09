package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import java.awt.Color;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Contains basic GuiElement features which every GuiElement needs to have.
 * @author Mike Reiche
 */
public interface BasicUiElement {
    QuantityAssertion<Integer> numberOfElements();
    BinaryAssertion<Boolean> present();
    BinaryAssertion<Boolean> displayed();
    BinaryAssertion<Boolean> visible(boolean complete);
    StringAssertion<String> tagName();
    RectAssertion bounds();
    /**
     * Takes a screenshot of the current element
     */
    ImageAssertion screenshot();
    BasicUiElement highlight(Color color);
    default BasicUiElement highlight() {
        return highlight(new Color(0,0, 255, 255));
    }
    String createXPath();
    Locate getLocate();
    WebElement getWebElement();
    /**
     * Centers the element in the viewport
     */
    default BasicUiElement scrollIntoView() {
        return scrollIntoView(new Point(0,0));
    }

    /**
     * Centers the element in the viewport with a given offset
     * @param offset
     */
    BasicUiElement scrollIntoView(Point offset);
}
