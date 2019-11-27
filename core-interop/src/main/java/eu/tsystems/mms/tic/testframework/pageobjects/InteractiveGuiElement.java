package eu.tsystems.mms.tic.testframework.pageobjects;

public interface InteractiveGuiElement<SELF> extends TestableGuiElement<SELF> {
    InteractiveGuiElement select(Boolean select);
    InteractiveGuiElement click();
    InteractiveGuiElement clickJS();
    InteractiveGuiElement doubleClick();
    InteractiveGuiElement doubleClickJS();
    InteractiveGuiElement rightClick();
    InteractiveGuiElement rightClickJS();
    InteractiveGuiElement select();
    InteractiveGuiElement deselect();
    InteractiveGuiElement sendKeys(CharSequence... charSequences);
    /**
     * Performs actions as a user
     */
    InteractiveGuiElement asUser();
    InteractiveGuiElement clear();
    InteractiveGuiElement hover();
    //InteractiveGuiElement mouseOver();
    //InteractiveGuiElement mouseOverJS();
}
