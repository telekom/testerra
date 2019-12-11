package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;

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
    BasicUiElement highlight();
}
