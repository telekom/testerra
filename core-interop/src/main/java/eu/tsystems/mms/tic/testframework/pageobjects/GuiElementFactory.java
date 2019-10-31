package eu.tsystems.mms.tic.testframework.pageobjects;

public interface GuiElementFactory {
    IGuiElement create(
        Locate locator,
        IPage page
    );
    IGuiElement createFromAncestor(
        Locate locator,
        IGuiElement ancestor
    );
    IGuiElement createWithFrames(
        Locate locator,
        IGuiElement... frames
    );
}
