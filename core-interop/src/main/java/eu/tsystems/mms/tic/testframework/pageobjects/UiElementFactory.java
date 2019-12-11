package eu.tsystems.mms.tic.testframework.pageobjects;

public interface UiElementFactory {
    UiElement createWithPage(
        PageObject page,
        Locate locator
    );
    UiElement createFromParent(
        UiElement parent,
        Locate locator
    );
    UiElement createWithFrames(
        Locate locator,
        UiElement... frames
    );
}
