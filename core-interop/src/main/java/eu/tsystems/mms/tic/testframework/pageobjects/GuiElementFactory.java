package eu.tsystems.mms.tic.testframework.pageobjects;

public interface GuiElementFactory {
    IGuiElement create(
        PageObject page,
        Locate locator
    );
    IGuiElement createWithParent(
        IGuiElement parent,
        Locate locator
    );
    IGuiElement createWithFrames(
        Locate locator,
        IGuiElement... frames
    );
}
