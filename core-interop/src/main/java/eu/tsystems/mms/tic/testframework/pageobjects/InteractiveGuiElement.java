package eu.tsystems.mms.tic.testframework.pageobjects;

public interface InteractiveGuiElement extends TestableGuiElement {
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
     * Sends keys with a maximum of characters per minute
     */
    InteractiveGuiElement userSendKeys(int cpm, CharSequence... charSequences);
    InteractiveGuiElement clear();
    InteractiveGuiElement hover();
    //InteractiveGuiElement mouseOver();
    //InteractiveGuiElement mouseOverJS();
}
