package eu.tsystems.mms.tic.testframework.pageobjects;

public interface GuiElementFactory {
    IGuiElement create(
        Locate locator,
        PageObject page
    );
    IGuiElement createWithAncestor(
        Locate locator,
        IGuiElement ancestor
    );
    IGuiElement createWithFrames(
        Locate locator,
        IGuiElement... frames
    );
}
