package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;

/**
 * All interactions that can be performed on a GuiElement
 * @author Mike Reiche
 */
public interface GuiElementActions extends TestableGuiElement {
    /**
     * Select/Deselect a selectable element.
     */
    default GuiElementActions select(boolean select) {
        if (select) {
            return select();
        } else {
            return deselect();
        }
    }
    GuiElementActions click();
    GuiElementActions doubleClick();
    GuiElementActions rightClick();
    GuiElementActions select();
    GuiElementActions deselect();
    GuiElementActions sendKeys(CharSequence... charSequences);
    GuiElementActions clear();
    GuiElementActions hover();
    /**
     * This method scrolls to the element with an given offset.
     */
    GuiElementActions scrollTo(int yOffset);

    default GuiElementActions scrollTo() {
        return scrollTo(0);
    }

    //InteractiveGuiElement clickJS();
    //InteractiveGuiElement doubleClickJS();
    //InteractiveGuiElement rightClickJS();
    //InteractiveGuiElement mouseOver();
    //InteractiveGuiElement mouseOverJS();
}
