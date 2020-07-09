package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

/**
 * All interactions that can be performed on a {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementActions extends TestableUiElement {
    /**
     * Select/Deselect a selectable element.
     */
    default UiElementActions select(boolean select) {
        if (select) {
            return select();
        } else {
            return deselect();
        }
    }
    UiElementActions click();
    UiElementActions doubleClick();
    UiElementActions contextClick();
    UiElementActions select();
    UiElementActions deselect();
    UiElementActions sendKeys(CharSequence... charSequences);
    UiElementActions type(String text);
    UiElementActions clear();
    UiElementActions hover();
}
